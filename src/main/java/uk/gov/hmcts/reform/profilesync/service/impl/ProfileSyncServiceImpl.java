package uk.gov.hmcts.reform.profilesync.service.impl;

import feign.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.profilesync.client.IdamClient;
import uk.gov.hmcts.reform.profilesync.config.TokenConfigProperties;
import uk.gov.hmcts.reform.profilesync.domain.ErrorResponse;
import uk.gov.hmcts.reform.profilesync.domain.Source;
import uk.gov.hmcts.reform.profilesync.domain.SyncJobAudit;
import uk.gov.hmcts.reform.profilesync.domain.User;
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
        log.info("Inside getSyncFeed");
        List<IdamClient.User> totalUsers = new ArrayList<>();

        Map<String, String> formParams = new HashMap<>();
        formParams.put("query", searchQuery);
        Response response  = idamClient.getUserFeed(bearerToken, formParams);
        ResponseEntity responseEntity = JsonFeignResponseHelper.toResponseEntity(response, IdamClient.User.class);
        Class clazz = response.status() > 300 ? ErrorResponse.class : IdamClient.User.class;

        if ( response.status() < 300 && responseEntity.getStatusCode().is2xxSuccessful()) {

            int records = Integer.parseInt(responseEntity.getHeaders().get("X-Total-Count").get(0));
            if (records > 20) {

                for (Integer i : numberOfCallsToIdamService(records)) {

                    callToGetUserFeed(bearerToken,i,formParams);
                    //totalUsers.addAll(callToGetUserFeed(bearerToken,i,formParams));

                }

                log.info("Total IDAM Users Count::" + totalUsers.size());
            } else {

                totalUsers =  (List<IdamClient.User>) responseEntity.getBody();

            }


        }

        if(CollectionUtils.isEmpty(totalUsers)) {
            SyncJobAudit syncJobAudit = new SyncJobAudit(200, "success", Source.SYNC);
            syncJobRepository.save(syncJobAudit);
        }
        return totalUsers;
    }

    private List<IdamClient.User> callToGetUserFeed(String token, Integer pageNo, Map<String, String> formParams) {

        String newQuery = "roles:\"pui-case-manager\" OR roles:\"pui-user-manager\" OR roles:\"pui-organisation-manager\" OR roles:\"pui-finance-manager\"$PAGE$";
        String dynamicQuery = newQuery.replace("$PAGE$","&page="+pageNo);
        log.info("dynamicQuery::" + dynamicQuery);
        formParams.put("query",dynamicQuery);
        List<IdamClient.User> usersPerPage = new ArrayList<>();
        List<IdamClient.User> users = null;
        Response response  = idamClient.getUserFeed(token, formParams);
        ResponseEntity responseEntity = JsonFeignResponseHelper.toResponseEntity(response, usersPerPage.getClass());
        Class clazz = response.status() > 300 ? ErrorResponse.class : IdamClient.User.class;

        if ( response.status() < 300 && responseEntity.getStatusCode().is2xxSuccessful()) {

             users = (List<IdamClient.User>) responseEntity.getBody();
             log.info("User Size::" + users.size());
            responseEntity = null;
            usersPerPage = null;
        }
     return  users;
    }

    private List<Integer> numberOfCallsToIdamService(int totalRecords) {

        List<Integer> pages =  new ArrayList<>();
        int pageCount = totalRecords/20;
        int index = 0;
        if (totalRecords%20 > 0) {
            pageCount = pageCount + 1;
        }
        while(index<pageCount) {
            pages.add(index);
            index ++;
        }
        return pages;
    }

    public void updateUserProfileFeed(String searchQuery, int recordsCount) {
        log.info("Inside updateUserProfileFeed");
        String bearerToken = BEARER + getBearerToken();
        getSyncFeed(bearerToken, searchQuery);
        //profileUpdateService.updateUserProfile(searchQuery, bearerToken, getS2sToken(), getSyncFeed(bearerToken, searchQuery), recordsCount);
        log.info("After updateUserProfileFeed");
    }
}
