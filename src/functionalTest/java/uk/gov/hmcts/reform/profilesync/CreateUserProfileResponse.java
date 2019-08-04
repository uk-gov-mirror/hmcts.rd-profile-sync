package uk.gov.hmcts.reform.profilesync;

import static java.util.Objects.requireNonNull;

import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.hmcts.reform.profilesync.domain.UserProfile;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserProfileResponse {

    private UUID idamId;
    private Integer idamRegistrationResponse;

    public CreateUserProfileResponse(UserProfile userProfile) {

        requireNonNull(userProfile, "userProfile must not be null");
        this.idamId = userProfile.getIdamId();
        this.idamRegistrationResponse = userProfile.getIdamRegistrationResponse();
    }
}

