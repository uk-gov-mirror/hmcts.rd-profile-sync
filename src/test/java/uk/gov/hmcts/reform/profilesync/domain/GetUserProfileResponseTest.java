package uk.gov.hmcts.reform.profilesync.domain;

import org.junit.Before;
import org.junit.Test;
import uk.gov.hmcts.reform.profilesync.helper.MockDataProvider;

import static org.assertj.core.api.Assertions.assertThat;

public class GetUserProfileResponseTest {

    private UserProfile userProfile;
    private GetUserProfileResponse sut;

    @Before
    public void setUp() {
        userProfile = MockDataProvider.getUserProfile();
        sut = new GetUserProfileResponse(userProfile);
    }

    @Test
    public void getIdamId() {
        assertThat(sut.getIdamId()).isEqualTo(userProfile.getIdamId());
    }

    @Test
    public void getEmail() {
        assertThat(sut.getEmail()).isEqualTo(userProfile.getEmail());
    }

    @Test
    public void getFirstName() {
        assertThat(sut.getFirstName()).isEqualTo(userProfile.getFirstName());
    }

    @Test
    public void getLastName() {
        assertThat(sut.getLastName()).isEqualTo(userProfile.getLastName());
    }


    @Test
    public void getIdamStatus() {
        assertThat(sut.getIdamStatus()).isEqualTo(userProfile.getStatus());
    }
}