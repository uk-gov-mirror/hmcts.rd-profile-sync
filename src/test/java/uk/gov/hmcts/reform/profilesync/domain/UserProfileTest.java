package uk.gov.hmcts.reform.profilesync.domain;

import org.junit.Test;
import uk.gov.hmcts.reform.profilesync.helper.MockDataProvider;

import static org.assertj.core.api.Assertions.assertThat;


public class UserProfileTest {

    private UserProfile sut = MockDataProvider.getUserProfile();

    @Test
    public void testGetIdamId() {
        assertThat(sut.getIdamId()).isEqualTo(MockDataProvider.idamId);
    }

    @Test
    public void testGetEmail() {
        assertThat(sut.getEmail()).isEqualTo(MockDataProvider.email);
    }

    @Test
    public void testGetFirstName() {
        assertThat(sut.getFirstName()).isEqualTo(MockDataProvider.firstName);
    }

    @Test
    public void testGetLastName() {
        assertThat(sut.getLastName()).isEqualTo(MockDataProvider.lastName);
    }

    @Test
    public void testIsEmailCommsConsent() {
        assertThat(sut.isEmailCommsConsent()).isEqualTo(MockDataProvider.emailCommsConsent);
    }

    @Test
    public void testGetEmailCommsConsentTs() {
        assertThat(sut.getEmailCommsConsentTs()).isEqualTo(MockDataProvider.currentTime);
    }

    @Test
    public void testIsPostalCommsConsent() {
        assertThat(sut.isPostalCommsConsent()).isEqualTo(MockDataProvider.postalCommsConsent);
    }

    @Test
    public void testGetPostalCommsConsentTs() {
        assertThat(sut.getPostalCommsConsentTs()).isEqualTo(MockDataProvider.currentTime);
    }

    @Test
    public void testGetStatus() {
        assertThat(sut.getStatus()).isEqualTo(MockDataProvider.status);
    }

    @Test
    public void testGetIdamRegistrationResponse() {
        assertThat(sut.getIdamRegistrationResponse()).isEqualTo(MockDataProvider.idamRegistrationResponse);
    }

    @Test
    public void testGetCreated() {
        assertThat(sut.getCreated()).isEqualTo(MockDataProvider.currentTime);
    }

    @Test
    public void testGetLastUpdated() {
        assertThat(sut.getLastUpdated()).isEqualTo(MockDataProvider.currentTime);
    }

    @Test
    public void builder() {
        long expectId = 1222L;
        UserProfile userProfile = UserProfile.builder().id(1222L).build();

        assertThat(userProfile.getId()).isEqualTo(expectId);
    }
}