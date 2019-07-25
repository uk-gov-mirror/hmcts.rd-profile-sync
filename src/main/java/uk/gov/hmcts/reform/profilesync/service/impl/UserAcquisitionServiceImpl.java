package uk.gov.hmcts.reform.profilesync.service.impl;

import feign.FeignException;
import feign.Response;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.profilesync.client.UserProfileClient;
import uk.gov.hmcts.reform.profilesync.domain.GetUserProfileResponse;
import uk.gov.hmcts.reform.profilesync.service.UserAcquisitionService;
import uk.gov.hmcts.reform.profilesync.util.JsonFeignResponseHelper;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class UserAcquisitionServiceImpl implements UserAcquisitionService {

    @Autowired
    private final UserProfileClient userProfileClient;

    public Optional<GetUserProfileResponse> findUser(String bearerToken, String s2sToken, String id){

        GetUserProfileResponse userProfile = null;
        try {
            Response response = userProfileClient.findUser(bearerToken, s2sToken, id);
            ResponseEntity responseEntity = JsonFeignResponseHelper.toResponseEntity(response, GetUserProfileResponse.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                userProfile = (GetUserProfileResponse) responseEntity.getBody();
                log.info("Found record in User Profile with idamId = {}", null != userProfile ? userProfile.getIdamId() : "null");
            }
        } catch (FeignException ex) {
            //Do nothing, but log or insert an audit record.
            log.info("Exception occurred : Status - {}, Content - {}", ex.status(), ex.contentUTF8());
        }

        return Optional.ofNullable(userProfile);
    }
}
