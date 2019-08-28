package uk.gov.hmcts.reform.profilesync.domain;

import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserProfileResponse {

    private UUID idamId;
    private Integer idamRegistrationResponse;

    public UserProfileResponse(UserProfile userProfile) {

        this.idamId = userProfile.getIdamId();
        this.idamRegistrationResponse = userProfile.getIdamRegistrationResponse();
    }
}
