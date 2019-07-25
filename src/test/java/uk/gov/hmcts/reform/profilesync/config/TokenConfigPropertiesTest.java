package uk.gov.hmcts.reform.profilesync.config;

import org.junit.Test;
import uk.gov.hmcts.reform.profilesync.helper.MockDataProvider;

import static org.assertj.core.api.Assertions.assertThat;


public class TokenConfigPropertiesTest {


    private TokenConfigProperties sut = new TokenConfigProperties();

    @Test
    public void getClientId() {
        sut.setClientId(MockDataProvider.clientId);
        assertThat(sut.getClientId()).isEqualTo(MockDataProvider.clientId);
    }

    @Test
    public void getClientSecret() {
        sut.setClientSecret(MockDataProvider.clientSecret);
        assertThat(sut.getClientSecret()).isEqualTo(MockDataProvider.clientSecret);
    }

    @Test
    public void getRedirectUri() {
        sut.setRedirectUri(MockDataProvider.redirectUri);
        assertThat(sut.getRedirectUri()).isEqualTo(MockDataProvider.redirectUri);
    }

    @Test
    public void getAuthorization() {
        sut.setAuthorization(MockDataProvider.authorization);
        assertThat(sut.getAuthorization()).isEqualTo(MockDataProvider.authorization);
    }

    @Test
    public void getClientAuthorization() {
        sut.setClientAuthorization(MockDataProvider.clientAuthorization);
        assertThat(sut.getClientAuthorization()).isEqualTo(MockDataProvider.clientAuthorization);
    }
}