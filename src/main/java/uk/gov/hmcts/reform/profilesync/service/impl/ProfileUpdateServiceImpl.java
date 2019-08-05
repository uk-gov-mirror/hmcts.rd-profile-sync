package uk.gov.hmcts.reform.profilesync.service.impl;

import feign.FeignException;
import feign.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.profilesync.client.IdamClient;
import uk.gov.hmcts.reform.profilesync.client.UserProfileClient;
import uk.gov.hmcts.reform.profilesync.domain.UserProfileResponse;
import uk.gov.hmcts.reform.profilesync.domain.ErrorResponse;
import uk.gov.hmcts.reform.profilesync.domain.GetUserProfileResponse;
import uk.gov.hmcts.reform.profilesync.domain.IdamStatus;
import uk.gov.hmcts.reform.profilesync.domain.Source;
import uk.gov.hmcts.reform.profilesync.domain.SyncJobAudit;
import uk.gov.hmcts.reform.profilesync.domain.UserProfile;
import uk.gov.hmcts.reform.profilesync.repository.SyncJobRepository;
import uk.gov.hmcts.reform.profilesync.service.ProfileUpdateService;
import uk.gov.hmcts.reform.profilesync.service.UserAcquisitionService;
import uk.gov.hmcts.reform.profilesync.util.JsonFeignResponseHelper;

@Slf4j
@AllArgsConstructor
@Service
public class ProfileUpdateServiceImpl implements ProfileUpdateService {

    @Autowired
    protected   UserAcquisitionService userAcquisitionService;

    @Autowired
    private final UserProfileClient userProfileClient;

    @Autowired
    private final SyncJobRepository syncJobRepository;

    public void updateUserProfile(String searchQuery, String bearerToken, String s2sToken, List<IdamClient.User> users) throws Exception {
        log.info("In side updateUserProfile:: ");
        users.forEach(user -> {
            Optional<GetUserProfileResponse> userProfile = userAcquisitionService.findUser(bearerToken, s2sToken, user.getId().toString());
            if (userProfile.isPresent()) {
                Map<String, Boolean> status = new HashMap<String, Boolean>();
                status.put(IdamStatus.ACTIVE.name(), user.isActive());
                status.put(IdamStatus.PENDING.name(), user.isPending());
                status.put(IdamStatus.LOCKED.name(), user.isLocked());
                UserProfile updatedUserProfile = UserProfile.builder()
                        .email(user.getEmail())
                        .firstName(user.getForename())
                        .lastName(user.getSurname())
                        .idamStatus(iDamStatusResolver().get(status) != null ? iDamStatusResolver().get(status).name() : "PENDING")
                        .build();

                try{

                    syncUser(bearerToken,s2sToken,user.getId().toString(),updatedUserProfile);

                } catch (Exception e) {
                    log.error("User Not updated : Id - {}", user.getId().toString());
                    e.printStackTrace();
                }
                log.info("User updated : Id - {}", user.getId().toString());
            }
        });
    }

    private void syncUser(String bearerToken, String s2sToken,
                                                   String userId, UserProfile updatedUserProfile)throws Exception {

        try {
            Response response = userProfileClient.syncUserStatus(bearerToken, s2sToken, userId, updatedUserProfile);

            ResponseEntity responseEntity = JsonFeignResponseHelper.toResponseEntity(response, UserProfileResponse.class);

            Class clazz = response.status() > 300 ? ErrorResponse.class : UserProfileResponse.class;

            if (response.status() > 300) {

                log.error("Exception occurred : Status - {}", response.status());
                saveSyncJobAudit(response.status(),"fail");
               throw new Exception();
            }
        } catch (FeignException ex) {
            log.error("Exception occurred : Status - {}, Content - {}", ex.status());
            saveSyncJobAudit(500,"fail");
            throw new Exception();
        }

    }

    private void saveSyncJobAudit(Integer iDamResponse,String message) {

        SyncJobAudit syncJobAudit = new SyncJobAudit(iDamResponse, message, Source.SYNC);
        syncJobRepository.save(syncJobAudit);
    }

    public Map<Map<String, Boolean>, IdamStatus> iDamStatusResolver() {

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
