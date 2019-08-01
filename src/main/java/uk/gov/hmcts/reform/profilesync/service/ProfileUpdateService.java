package uk.gov.hmcts.reform.profilesync.service;

import java.util.List;
import java.util.Optional;

import uk.gov.hmcts.reform.profilesync.client.IdamClient;
import uk.gov.hmcts.reform.profilesync.domain.SyncJobAudit;

public interface ProfileUpdateService {

    void updateUserProfile(String searchQuery, String bearerToken, String s2sToken, List<IdamClient.User> users, int count);

    List<SyncJobAudit> findByStatus(String status);
}
