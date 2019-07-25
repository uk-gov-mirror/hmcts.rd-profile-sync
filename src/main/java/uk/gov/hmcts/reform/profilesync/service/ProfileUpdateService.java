package uk.gov.hmcts.reform.profilesync.service;

import uk.gov.hmcts.reform.profilesync.client.IdamClient;

import java.util.List;

public interface ProfileUpdateService {

    void updateUserProfile(String searchQuery, String bearerToken, String s2sToken, List<IdamClient.User> users);
}
