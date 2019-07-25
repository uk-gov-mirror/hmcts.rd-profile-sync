package uk.gov.hmcts.reform.profilesync.service;

import feign.FeignException;
import feign.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.profilesync.client.IdamClient;
import uk.gov.hmcts.reform.profilesync.client.UserProfileClient;
import uk.gov.hmcts.reform.profilesync.config.TokenConfigProperties;
import uk.gov.hmcts.reform.profilesync.domain.CreateUserProfileResponse;
import uk.gov.hmcts.reform.profilesync.domain.GetUserProfileResponse;
import uk.gov.hmcts.reform.profilesync.domain.UserProfile;
import uk.gov.hmcts.reform.profilesync.util.JsonFeignResponseHelper;

import javax.validation.constraints.NotNull;
import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class ProfileSyncService {

    @Autowired
    private final IdamClient idamClient;

    @Autowired
    private final AuthTokenGenerator tokenGenerator;

    @Autowired
    private final UserProfileClient userProfileClient;

    @Autowired
    private final TokenConfigProperties props;


    public static final String BASIC = "Basic ";
    public static final String BEARER = "Bearer ";

    public String authorize(){

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

    public String getS2sToken(){
        return tokenGenerator.generate();
    }

    public List<IdamClient.User> getSyncFeed(String bearerToken, String searchQuery) {

        Map<String, String> formParams = new HashMap<>();
        formParams.put("query", searchQuery);
        List<IdamClient.User> response = idamClient.getUserFeed(bearerToken, formParams);
        return response;
    }

    public void updateUserProfileFeed(String searchQuery){
        String bearerToken = BEARER + getBearerToken();
        String s2sToken = getS2sToken();
        List<IdamClient.User> users = getSyncFeed(bearerToken, searchQuery);

        users.forEach(user -> {
            Optional<GetUserProfileResponse> userProfile = findUser(bearerToken, s2sToken, user.getId().toString());
            if (userProfile.isPresent()){

                UserProfile updatedUserProfile = UserProfile.builder()
                                                .email(user.getEmail())
                                                .firstName(user.getForename())
                                                .lastName(user.getSurname())
                                                .status(user.isActive() ? "ACTIVE" : "PENDING")
                                                .build();

                log.info("User updated : Id - {}", user.getId().toString());
            }
        });

    }

    private Optional<GetUserProfileResponse> findUser(String bearerToken, String s2sToken, String id){

        GetUserProfileResponse userProfile = null;
        try {
            Response response = userProfileClient.findUser(bearerToken, s2sToken, id);
            ResponseEntity responseEntity = JsonFeignResponseHelper.toResponseEntity(response, GetUserProfileResponse.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                userProfile = (GetUserProfileResponse) responseEntity.getBody();
                log.info("Found record in User Profile with idamId = {}", userProfile.getIdamId());
            }
        } catch (FeignException ex) {
            //Do nothing, but log or insert an audit record.
            log.info("Exception occurred : Status - {}, Content - {}", ex.status(), ex.contentUTF8());
        }

        return Optional.ofNullable(userProfile);
    }


    private Optional<CreateUserProfileResponse> syncUser(String bearerToken, String s2sToken,
                                                         String userId, UserProfile updatedUserProfile) {
        CreateUserProfileResponse userProfile = null;

        try {
            Response response = userProfileClient.syncUserStatus(bearerToken, s2sToken, userId, updatedUserProfile);
            ResponseEntity responseEntity = JsonFeignResponseHelper.toResponseEntity(response, CreateUserProfileResponse.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                userProfile = (CreateUserProfileResponse) responseEntity.getBody();
                log.info("User record updated in User Profile with idamId = {}", userProfile.getIdamId());
            }
        } catch (FeignException ex) {
            log.info("Exception occurred : Status - {}, Content - {}", ex.status(), ex.contentUTF8());
        }

        return Optional.ofNullable(userProfile);
    }
}
