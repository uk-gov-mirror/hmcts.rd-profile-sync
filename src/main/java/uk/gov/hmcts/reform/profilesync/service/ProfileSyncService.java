package uk.gov.hmcts.reform.profilesync.service;

import uk.gov.hmcts.reform.profilesync.client.IdamClient;

import java.util.List;

public interface ProfileSyncService {

    String authorize();

    String getBearerToken();

    String getS2sToken();

    List<IdamClient.User> getSyncFeed(String bearerToken, String searchQuery);

    void updateUserProfileFeed(String searchQuery);

}
