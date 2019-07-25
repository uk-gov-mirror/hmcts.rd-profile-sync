package uk.gov.hmcts.reform.profilesync.service;

import uk.gov.hmcts.reform.profilesync.domain.GetUserProfileResponse;

import java.util.Optional;

public interface UserAcquisitionService {

    Optional<GetUserProfileResponse> findUser(String bearerToken, String s2sToken, String id);

}
