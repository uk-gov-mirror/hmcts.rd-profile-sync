package uk.gov.hmcts.reform.profilesync.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.profilesync.client.IdamClient;
import uk.gov.hmcts.reform.profilesync.domain.GetUserProfileResponse;
import uk.gov.hmcts.reform.profilesync.domain.UserProfile;
import uk.gov.hmcts.reform.profilesync.service.ProfileUpdateService;
import uk.gov.hmcts.reform.profilesync.service.UserAcquisitionService;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class ProfileUpdateServiceImpl implements ProfileUpdateService {

    @Autowired
    private final UserAcquisitionService userAcquisitionService;

    public void updateUserProfile(String searchQuery, String bearerToken, String s2sToken, List<IdamClient.User> users) {
        users.forEach(user -> {
            Optional<GetUserProfileResponse> userProfile = userAcquisitionService.findUser(bearerToken, s2sToken, user.getId().toString());
            if (userProfile.isPresent()){

                UserProfile updatedUserProfile = UserProfile.builder()
                        .email(user.getEmail())
                        .firstName(user.getForename())
                        .lastName(user.getSurname())
                        .status(user.isActive() ? "ACTIVE" : "PENDING")
                        .build();

                log.info("User updated : Id - {}", user.getId().toString());
            }
        });
    }

}
