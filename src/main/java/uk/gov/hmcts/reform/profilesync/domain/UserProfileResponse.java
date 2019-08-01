package uk.gov.hmcts.reform.profilesync.domain;

import static java.util.Objects.requireNonNull;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserProfileResponse {

    private UUID idamId;
    private Integer iDamRegistrationResponse;

    public UserProfileResponse(UserProfile userProfile) {

        this.idamId = userProfile.getIdamId();
        this.iDamRegistrationResponse = userProfile.getIdamRegistrationResponse();
    }
}
