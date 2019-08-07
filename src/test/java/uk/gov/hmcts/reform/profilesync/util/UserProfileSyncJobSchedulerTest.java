package uk.gov.hmcts.reform.profilesync.util;

import org.junit.Test;
import uk.gov.hmcts.reform.profilesync.repository.SyncJobRepository;
import uk.gov.hmcts.reform.profilesync.service.ProfileSyncService;

import static org.mockito.Mockito.mock;

public class UserProfileSyncJobSchedulerTest {

    ProfileSyncService profileSyncServiceMock = mock(ProfileSyncService.class);

    SyncJobRepository syncJobRepository = mock(SyncJobRepository.class);
    
    @Test
    public void testGetLastBatchFailureTimeInHours() {



    }

}
