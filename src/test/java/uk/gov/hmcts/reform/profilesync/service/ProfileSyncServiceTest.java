package uk.gov.hmcts.reform.profilesync.service;

import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.profilesync.client.IdamClient;
import uk.gov.hmcts.reform.profilesync.client.UserProfileClient;
import uk.gov.hmcts.reform.profilesync.config.TokenConfigProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


public class ProfileSyncServiceTest {

    private final IdamClient idamClientMock = Mockito.mock(IdamClient.class);

    private final AuthTokenGenerator tokenGeneratorMock = Mockito.mock(AuthTokenGenerator.class);

    private final UserProfileClient userProfileClientMock = Mockito.mock(UserProfileClient.class);

    private final TokenConfigProperties propsMock = Mockito.mock(TokenConfigProperties.class);

    private ProfileSyncService sut = new ProfileSyncService(idamClientMock, tokenGeneratorMock, userProfileClientMock, propsMock);

//    @Ignore
//    @Test
//    public void testAuthorize() {
//        String expectResult = "code";
//        IdamClient.AuthenticateUserResponse responseMock = Mockito.mock(IdamClient.AuthenticateUserResponse.class);
//
//        when(responseMock.getCode()).thenReturn(expectResult);
//        when(idamClientMock.authorize(any(String.class), any(Map.class), any(String.class))).thenReturn(responseMock);
//
//        String result = sut.authorize();
//
//        assertEquals(result, expectResult);
//    }

    @Test
    public void getBearerToken() {
        final String clientId = "234342332";
        final String redirectUri = "http://someurl.com";

        final String authorization = "my authorization";
        final String clientAuth = "client authorized";
        final String expectCode = "dd5g2b6-9699-12f9-bf42-526rf8864g64";

        when(propsMock.getClientId()).thenReturn(clientId);
        when(propsMock.getRedirectUri()).thenReturn(redirectUri);
        when(propsMock.getAuthorization()).thenReturn(authorization);
        when(propsMock.getClientAuthorization()).thenReturn(clientAuth);

        Map<String, String> params = new HashMap<>();
        params.put("client_id", propsMock.getClientId());
        params.put("redirect_uri", propsMock.getRedirectUri());
        params.put("response_type", "code");
        params.put("scope", "openid profile roles create-user manage-user search-user");

        IdamClient.AuthenticateUserResponse authenticateUserResponseMock = Mockito.mock(IdamClient.AuthenticateUserResponse.class);

        when(idamClientMock.authorize(
                ProfileSyncService.BASIC + propsMock.getAuthorization(), params, ""))
                .thenReturn(authenticateUserResponseMock);

        when(authenticateUserResponseMock.getCode()).thenReturn(expectCode);

        Map<String, String> formParams = new HashMap<>();
        formParams.put("client_id", propsMock.getClientId());
        formParams.put("redirect_uri", propsMock.getRedirectUri());
        formParams.put("code", expectCode);
        formParams.put("grant_type", "authorization_code");

        IdamClient.TokenExchangeResponse responseMock = Mockito.mock(IdamClient.TokenExchangeResponse.class);

        when(idamClientMock.getToken(ProfileSyncService.BASIC + clientAuth, formParams, "")).thenReturn(responseMock);
        when(responseMock.getAccessToken()).thenReturn(expectCode);


        String actualToken = sut.getBearerToken();


        assertThat(actualToken).isEqualTo(expectCode);
    }

    @Test
    public void testGetS2sToken() {
        final String expect = "Bearer xyz" ;
        when(tokenGeneratorMock.generate()).thenReturn(expect);

        assertThat(sut.getS2sToken()).isEqualTo(expect);

    }

    @Test
    public void testGetSyncFeed() {
        final String bearerToken = "Bearer eyJ0eXAiOiJKV1QiLCJ6aXAiOiJOT05FIiwia2lkIjoicDBZL0lpN0txdS9uZndIK0RvdmZVMEszbHRJPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJwcmF2ZWVuLnRob3R0ZW1wdWRpQGhtY3RzLm5ldCIsImF1dGhfbGV2ZWwiOjAsImF1ZGl0VHJhY2tpbmdJZCI6IjIxOWNiMjdkLTkwZmEtNGE3Yi05Yzk1LWRhNWRhMzI0MjJlNyIsImlzcyI6Imh0dHBzOi8vZm9yZ2Vyb2NrLWFtLnNlcnZpY2UuY29yZS1jb21wdXRlLWlkYW0tcHJldmlldy5pbnRlcm5hbDo4NDQzL29wZW5hbS9vYXV0aDIvaG1jdHMiLCJ0b2tlbk5hbWUiOiJhY2Nlc3NfdG9rZW4iLCJ0b2tlbl90eXBlIjoiQmVhcmVyIiwiYXV0aEdyYW50SWQiOiJkYzFkM2NjNS05OWZhLTRhNDAtYWIxMC0zMzcxZjcwNjQ0YWMiLCJhdWQiOiJyZC1wcm9mZXNzaW9uYWwtYXBpIiwibmJmIjoxNTYzNTMyMDY4LCJncmFudF90eXBlIjoiYXV0aG9yaXphdGlvbl9jb2RlIiwic2NvcGUiOlsib3BlbmlkIiwicHJvZmlsZSIsInJvbGVzIiwiY3JlYXRlLXVzZXIiLCJtYW5hZ2UtdXNlciIsInNlYXJjaC11c2VyIl0sImF1dGhfdGltZSI6MTU2MzUzMjA0NzAwMCwicmVhbG0iOiIvaG1jdHMiLCJleHAiOjE1NjM1NjA4NjgsImlhdCI6MTU2MzUzMjA2OCwiZXhwaXJlc19pbiI6Mjg4MDAsImp0aSI6ImU3NjMyNzgwLWM1N2QtNDhjMC1iNmQ3LTZmM2M2MzIwMDExMiJ9.HmhKLluiDncAcHrZSvrgFxnagIFPVRXa9aSl8uymK3l91Ss94csZpDyUh5UQH0bzZtJjeNPAci5dEYgLuqeO9-ydhRA_tBhfbjS7kBlUmo6RHK492O5WE7goMhpsx-j6KoJ94-cNDgnbk0YyGYzPODS7FLx1HZgLH5_E7sIzT8-GXTMQOm7rwxFkcA0BZwgp45YYV1wvxRP6vCOpbY0Zrp8hx6fQdbpAste0BFOruOSXR9eLuqCCxX_wud7dv9FHF6wCBf_nZ6XkgVTZAHav0tWsLnfGMyEXwm5BJWKy3ctl8H8RQNPArA1PlhdkQBNYWk2BX_1zC7zFI_terWkqjQ";
        final String searchQuery = "lastModified:>now-24h";

        List<IdamClient.User> response = sut.getSyncFeed(bearerToken, searchQuery);

        assertThat(response).isNotNull();
    }

    @Test
    public void testUpdateUserProfileFeed() {
        final String bearerToken = "Bearer eyJ0eXAiOiJKV1QiLCJ6aXAiOiJOT05FIiwia2lkIjoicDBZL0lpN0txdS9uZndIK0RvdmZVMEszbHRJPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJwcmF2ZWVuLnRob3R0ZW1wdWRpQGhtY3RzLm5ldCIsImF1dGhfbGV2ZWwiOjAsImF1ZGl0VHJhY2tpbmdJZCI6IjIxOWNiMjdkLTkwZmEtNGE3Yi05Yzk1LWRhNWRhMzI0MjJlNyIsImlzcyI6Imh0dHBzOi8vZm9yZ2Vyb2NrLWFtLnNlcnZpY2UuY29yZS1jb21wdXRlLWlkYW0tcHJldmlldy5pbnRlcm5hbDo4NDQzL29wZW5hbS9vYXV0aDIvaG1jdHMiLCJ0b2tlbk5hbWUiOiJhY2Nlc3NfdG9rZW4iLCJ0b2tlbl90eXBlIjoiQmVhcmVyIiwiYXV0aEdyYW50SWQiOiJkYzFkM2NjNS05OWZhLTRhNDAtYWIxMC0zMzcxZjcwNjQ0YWMiLCJhdWQiOiJyZC1wcm9mZXNzaW9uYWwtYXBpIiwibmJmIjoxNTYzNTMyMDY4LCJncmFudF90eXBlIjoiYXV0aG9yaXphdGlvbl9jb2RlIiwic2NvcGUiOlsib3BlbmlkIiwicHJvZmlsZSIsInJvbGVzIiwiY3JlYXRlLXVzZXIiLCJtYW5hZ2UtdXNlciIsInNlYXJjaC11c2VyIl0sImF1dGhfdGltZSI6MTU2MzUzMjA0NzAwMCwicmVhbG0iOiIvaG1jdHMiLCJleHAiOjE1NjM1NjA4NjgsImlhdCI6MTU2MzUzMjA2OCwiZXhwaXJlc19pbiI6Mjg4MDAsImp0aSI6ImU3NjMyNzgwLWM1N2QtNDhjMC1iNmQ3LTZmM2M2MzIwMDExMiJ9.HmhKLluiDncAcHrZSvrgFxnagIFPVRXa9aSl8uymK3l91Ss94csZpDyUh5UQH0bzZtJjeNPAci5dEYgLuqeO9-ydhRA_tBhfbjS7kBlUmo6RHK492O5WE7goMhpsx-j6KoJ94-cNDgnbk0YyGYzPODS7FLx1HZgLH5_E7sIzT8-GXTMQOm7rwxFkcA0BZwgp45YYV1wvxRP6vCOpbY0Zrp8hx6fQdbpAste0BFOruOSXR9eLuqCCxX_wud7dv9FHF6wCBf_nZ6XkgVTZAHav0tWsLnfGMyEXwm5BJWKy3ctl8H8RQNPArA1PlhdkQBNYWk2BX_1zC7zFI_terWkqjQ";
        final String searchQuery = "lastModified:>now-24h";

        Map<String, String> formParams = new HashMap<>();
        formParams.put("query", searchQuery);

        List<IdamClient.User> userData = new ArrayList<>();

        when(idamClientMock.getUserFeed(bearerToken, formParams)).thenReturn(userData);

        List<IdamClient.User> response = sut.getSyncFeed(bearerToken, searchQuery);

        assertThat(response).isNotNull();
        sut.updateUserProfileFeed(searchQuery);


    }


}