package uk.gov.hmcts.reform.profilesync.domain;

import org.junit.Test;
import uk.gov.hmcts.reform.profilesync.helper.MockDataProvider;

import static org.assertj.core.api.Assertions.assertThat;


public class CreateUserProfileResponseTest {

    private UserProfile user = MockDataProvider.getUserProfile();
    private CreateUserProfileResponse createUserProfileResponse = new CreateUserProfileResponse(user);


    @Test
    public void testGetIdamId() {
        assertThat(createUserProfileResponse.getIdamId()).isEqualTo(user.getIdamId());
    }

    @Test
    public void testGetIdamRegistrationResponse() {
        assertThat(createUserProfileResponse.getIdamRegistrationResponse()).isEqualTo(user.getIdamRegistrationResponse());
    }

}