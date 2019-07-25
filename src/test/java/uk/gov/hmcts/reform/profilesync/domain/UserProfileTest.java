package uk.gov.hmcts.reform.profilesync.domain;

import org.junit.Test;
import uk.gov.hmcts.reform.profilesync.helper.MockDataProvider;

import static org.assertj.core.api.Assertions.assertThat;


public class UserProfileTest {

    private UserProfile sut = MockDataProvider.getUserProfile();

    @Test
    public void getIdamId() {
        assertThat(sut.getIdamId()).isEqualTo(MockDataProvider.idamId);
    }

    @Test
    public void getEmail() {
        assertThat(sut.getEmail()).isEqualTo(MockDataProvider.email);
    }

    @Test
    public void getFirstName() {
        assertThat(sut.getFirstName()).isEqualTo(MockDataProvider.firstName);
    }

    @Test
    public void getLastName() {
        assertThat(sut.getLastName()).isEqualTo(MockDataProvider.lastName);
    }

    @Test
    public void isEmailCommsConsent() {
        assertThat(sut.isEmailCommsConsent()).isEqualTo(MockDataProvider.emailCommsConsent);
    }

    @Test
    public void getEmailCommsConsentTs() {
        assertThat(sut.getEmailCommsConsentTs()).isEqualTo(MockDataProvider.currentTime);
    }

    @Test
    public void isPostalCommsConsent() {
        assertThat(sut.isPostalCommsConsent()).isEqualTo(MockDataProvider.postalCommsConsent);
    }

    @Test
    public void getPostalCommsConsentTs() {
        assertThat(sut.getPostalCommsConsentTs()).isEqualTo(MockDataProvider.currentTime);
    }

    @Test
    public void getStatus() {
        assertThat(sut.getStatus()).isEqualTo(MockDataProvider.status);
    }

    @Test
    public void getIdamRegistrationResponse() {
        assertThat(sut.getIdamRegistrationResponse()).isEqualTo(MockDataProvider.idamRegistrationResponse);
    }

    @Test
    public void getCreated() {
        assertThat(sut.getCreated()).isEqualTo(MockDataProvider.currentTime);
    }

    @Test
    public void getLastUpdated() {
        assertThat(sut.getLastUpdated()).isEqualTo(MockDataProvider.currentTime);
    }

    @Test
    public void builder() {
        long expectId = 1222L;
        UserProfile userProfile = UserProfile.builder().id(1222L).build();

        assertThat(userProfile.getId()).isEqualTo(expectId);
    }
}