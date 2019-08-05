package uk.gov.hmcts.reform.profilesync;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.profilesync.util.UserProfileSyncJobScheduler;


@Slf4j
public class RunProfileSyncJobTest extends AuthorizationEnabledIntegrationTest {

    @Autowired
    UserProfileSyncJobScheduler profileSyncJobScheduler ;
    @SuppressWarnings("unchecked")
    @Test
    public void persists_and_update_user_details_and_status_with_idam_details() {
        userProfileCreateUserWireMock(HttpStatus.CREATED);
        profileSyncJobScheduler.updateIdamDataWithUserProfile();
        //userProfileCreateUserWireMock(HttpStatus.CREATED);
       // userProfileGetUserWireMock();


    }



}
