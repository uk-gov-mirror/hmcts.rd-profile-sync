package uk.gov.hmcts.reform.profilesync.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.profilesync.client.IdamClient;
import uk.gov.hmcts.reform.profilesync.domain.GetUserProfileResponse;
import uk.gov.hmcts.reform.profilesync.domain.IdamStatus;
import uk.gov.hmcts.reform.profilesync.domain.UserProfile;
import uk.gov.hmcts.reform.profilesync.service.ProfileUpdateService;
import uk.gov.hmcts.reform.profilesync.service.UserAcquisitionService;

@Slf4j
@AllArgsConstructor
@Service
public class ProfileUpdateServiceImpl implements ProfileUpdateService {

    @Autowired
    protected   UserAcquisitionService userAcquisitionService;

    public void updateUserProfile(String searchQuery, String bearerToken, String s2sToken, List<IdamClient.User> users) {
        users.forEach(user -> {
            Optional<GetUserProfileResponse> userProfile = userAcquisitionService.findUser(bearerToken, s2sToken, user.getId().toString());
            if (userProfile.isPresent()) {
                Map<String, Boolean> status = new HashMap<String, Boolean>();
                status.put(IdamStatus.ACTIVE.name(), user.isActive());
                status.put(IdamStatus.PENDING.name(), user.isPending());
                status.put(IdamStatus.LOCKED.name(), user.isLocked());
                UserProfile updatedUserProfile = UserProfile.builder()
                        //.email(user.getEmail())
                        .firstName(user.getForename())
                        .lastName(user.getSurname())
                        .status(idamStatusResolver().get(status) != null ? idamStatusResolver().get(status).name() : "PENDING")
                        .build();

                log.info("User updated : Id - {}", user.getId().toString());
            }
        });
    }

    public Map<Map<String, Boolean>, IdamStatus> idamStatusResolver() {

        Map<Map<String, Boolean>, IdamStatus> idamStatusMap = new HashMap<Map<String, Boolean>, IdamStatus>();
        idamStatusMap.put(addRule(false,true, false), IdamStatus.PENDING);
        idamStatusMap.put(addRule(true, false,false), IdamStatus.ACTIVE);
        idamStatusMap.put(addRule(true, false,true), IdamStatus.ACTIVE_AND_LOCKED);
        idamStatusMap.put(addRule(false,false,false), IdamStatus.SUSPENDED);
        idamStatusMap.put(addRule(false,false,true), IdamStatus.SUSPENDED_AND_LOCKED);
        idamStatusMap.put(addRule(false,false,true), IdamStatus.SUSPENDED_AND_LOCKED);

        return idamStatusMap;
    }

    public static Map<String, Boolean> addRule(boolean activeFlag, boolean pendingFlag, boolean lockedFlag) {
        Map<String, Boolean> pendingMapWithRules = new HashMap<>();
        pendingMapWithRules.put("ACTIVE", activeFlag);
        pendingMapWithRules.put("PENDING", pendingFlag);
        pendingMapWithRules.put("LOCKED", lockedFlag);
        return pendingMapWithRules;
    }

}
