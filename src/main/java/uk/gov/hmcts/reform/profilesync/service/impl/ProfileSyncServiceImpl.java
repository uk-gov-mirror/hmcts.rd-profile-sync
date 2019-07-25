package uk.gov.hmcts.reform.profilesync.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.profilesync.client.IdamClient;
import uk.gov.hmcts.reform.profilesync.config.TokenConfigProperties;
import uk.gov.hmcts.reform.profilesync.service.ProfileSyncService;
import uk.gov.hmcts.reform.profilesync.service.ProfileUpdateService;

import java.util.*;

@Service
@AllArgsConstructor
@Slf4j
public class ProfileSyncServiceImpl implements ProfileSyncService {

    @Autowired
    private final IdamClient idamClient;

    @Autowired
    private final AuthTokenGenerator tokenGenerator;

    @Autowired
    private final ProfileUpdateService profileUpdateService;

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
        profileUpdateService.updateUserProfile(searchQuery, bearerToken, s2sToken, getSyncFeed(bearerToken, searchQuery));
    }


}
