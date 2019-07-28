package uk.gov.hmcts.reform.profilesync.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.profilesync.service.impl.ProfileSyncServiceImpl;

@RestController
public class SyncController {

    @Autowired
    ProfileSyncServiceImpl service;

    @GetMapping(path = "/bearerToken", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getBearerToken() {

        return service.getBearerToken();
    }

    @GetMapping(path = "/token", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getS2sToken() {

        return service.getS2sToken();
    }

    @GetMapping(path = "/feed", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String getSyncFeed(@Value("${IDAM_SEARCH_QUERY:lastModified:>now-2h}") String searchQuery) {

        return service.getSyncFeed(ProfileSyncServiceImpl.BEARER + service.getBearerToken(), searchQuery).toString();
    }

    @GetMapping(path = "/update", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public String updateFeed(@Value("${IDAM_SEARCH_QUERY:lastModified:>now-24h}") String searchQuery) {

        service.updateUserProfileFeed(searchQuery);

        return "Done";
    }
}
