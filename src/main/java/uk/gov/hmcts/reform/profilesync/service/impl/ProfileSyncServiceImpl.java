package uk.gov.hmcts.reform.profilesync.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import feign.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.profilesync.client.IdamClient;
import uk.gov.hmcts.reform.profilesync.config.TokenConfigProperties;
import uk.gov.hmcts.reform.profilesync.domain.ErrorResponse;
import uk.gov.hmcts.reform.profilesync.domain.GetUserProfileResponse;
import uk.gov.hmcts.reform.profilesync.domain.Source;
import uk.gov.hmcts.reform.profilesync.domain.SyncJobAudit;
import uk.gov.hmcts.reform.profilesync.repository.SyncJobRepository;
import uk.gov.hmcts.reform.profilesync.service.ProfileSyncService;
import uk.gov.hmcts.reform.profilesync.service.ProfileUpdateService;
import uk.gov.hmcts.reform.profilesync.util.JsonFeignResponseHelper;


@Service
@AllArgsConstructor
@Slf4j
public class ProfileSyncServiceImpl implements ProfileSyncService {

    @Autowired
    protected final IdamClient idamClient;

    @Autowired
    protected final AuthTokenGenerator tokenGenerator;

    @Autowired
    protected final ProfileUpdateService profileUpdateService;

    @Autowired
    private final TokenConfigProperties props;

    @Autowired
    private final SyncJobRepository syncJobRepository;

    public String authorize() {

        Map<String, String> formParams = new HashMap<>();
        formParams.put("client_id", props.getClientId());
        formParams.put("redirect_uri", props.getRedirectUri());
        formParams.put("response_type", "code");
        formParams.put("scope", "openid profile roles create-user manage-user search-user");

        IdamClient.AuthenticateUserResponse response = idamClient.authorize(BASIC + props.getAuthorization(), formParams, "");

        return response.getCode();
    }

    public String getBearerToken() {

        Map<String, String> formParams = new HashMap<>();
        formParams.put("client_id", props.getClientId());
        formParams.put("redirect_uri", props.getRedirectUri());
        formParams.put("code", authorize());
        formParams.put("grant_type", "authorization_code");

        IdamClient.TokenExchangeResponse response = idamClient.getToken(BASIC + props.getClientAuthorization(), formParams, "");

        return response.getAccessToken();
    }

    public String getS2sToken() {
        return tokenGenerator.generate();
    }

    public List<IdamClient.User> getSyncFeed(String bearerToken, String searchQuery) {
        List<IdamClient.User> totalUsers = new ArrayList<>();

        Map<String, String> formParams = new HashMap<>();
        formParams.put("query", searchQuery);
        Response response = idamClient.getUserFeed(bearerToken, formParams);

        ResponseEntity responseEntity = JsonFeignResponseHelper.toResponseEntity(response, IdamClient.User.class);
        Class clazz = response.status() > 300 ? ErrorResponse.class : IdamClient.User.class;

        if (response.status() > 300) {

            log.error("No record find in the IDAM:{}");

        } else if (responseEntity.getStatusCode().is2xxSuccessful()) {

            List<String> values= responseEntity.getHeaders().get("X-Total-Count");
            List<IdamClient.User> users = (List<IdamClient.User>) responseEntity.getBody();

            totalUsers.addAll(users);

            log.info("Found record in User Profile with idamId = {}");

        }
        /*List<IdamClient.User>
        if(CollectionUtils.isEmpty(response)) {
            SyncJobAudit syncJobAudit = new SyncJobAudit(200, "success", Source.SYNC);
            syncJobRepository.save(syncJobAudit);
        }*/
        return totalUsers;
    }

    public void updateUserProfileFeed(String searchQuery, int recordsCount) {
        String bearerToken = BEARER + getBearerToken();
        profileUpdateService.updateUserProfile(searchQuery, bearerToken, getS2sToken(), getSyncFeed(bearerToken, searchQuery), recordsCount);
    }


}
