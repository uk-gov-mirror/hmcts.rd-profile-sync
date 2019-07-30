package uk.gov.hmcts.reform.profilesync.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.profilesync.service.ProfileSyncService;
import uk.gov.hmcts.reform.profilesync.service.impl.ProfileSyncServiceImpl;

@RequestMapping(
        path = "refdata/v1"
)
@RestController
public class SyncController {

    @Autowired
    protected ProfileSyncService profileSyncService;

    @GetMapping(path = "/bearerToken", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getBearerToken() {

        return profileSyncService.getBearerToken();
    }

    @GetMapping(path = "/token", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getS2sToken() {

        return profileSyncService.getS2sToken();
    }

    @GetMapping(path = "/feed", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getSyncFeed(@Value("${IDAM_SEARCH_QUERY:lastModified:>now-1h}") String searchQuery) {

        return profileSyncService.getSyncFeed(ProfileSyncServiceImpl.BEARER + profileSyncService.getBearerToken(), searchQuery).toString();
    }

    @GetMapping(path = "/update", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String updateFeed(@Value("${IDAM_SEARCH_QUERY:lastModified:>now-24h}") String searchQuery) {

        profileSyncService.updateUserProfileFeed(searchQuery);

        return "Done";
    }
}
