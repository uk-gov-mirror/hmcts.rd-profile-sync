package uk.gov.hmcts.reform.profilesync.client;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class IdamClientTest {


    @Test
    public void testAuthenticateUserResponse() {
        final String code = "my-code";
        IdamClient.AuthenticateUserResponse authenticateUserResponse = new IdamClient.AuthenticateUserResponse();
        authenticateUserResponse.setCode(code);

        assertThat(authenticateUserResponse.getCode()).isEqualTo(code);
    }

    @Test
    public void testTokenExchangeResponse() {
        String accessToken = "43890283490";
        IdamClient.TokenExchangeResponse tokenExchangeResponse = new IdamClient.TokenExchangeResponse();
        tokenExchangeResponse.setAccessToken(accessToken);

        assertThat(tokenExchangeResponse.getAccessToken()).isEqualTo(accessToken);
    }


}