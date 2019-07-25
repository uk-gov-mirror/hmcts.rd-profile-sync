package uk.gov.hmcts.reform.profilesync.controller;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import uk.gov.hmcts.reform.profilesync.client.IdamClient;
import uk.gov.hmcts.reform.profilesync.helper.MockDataProvider;
import uk.gov.hmcts.reform.profilesync.service.ProfileSyncService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class SyncControllerTest {
    private final String bearerToken = "Bearer eyJ0eXAiOiJKV1dIK0RvdPSIsImFsZyI6IlJTMjU2In0.jodi023j-ioj30.fu9230--j0wjfioefji0fu90";
    private final String s2sToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdYzOTIwMTA2fQ.lrQaX_fYOlaKDYcKu_TOgnAJxv4hSRM1aZrifxA";
    private final String searchQuery = "lastModified:>now-2h";
    final UUID idamId = UUID.randomUUID();
    final String feed = String.format("[IdamClient.User(active=true, email=apriloneil@hmcts.net, forename=April, " +
            "id=%s, lastModified=2019-07-01T08:16:38.917Z, locked=false, " +
            "pending=false, roles=[pui-user-manager], surname=ONeil)]", idamId);
    private ProfileSyncService serviceMock = Mockito.mock(ProfileSyncService.class);
    private final String updateFeedAck = "Done";

    @InjectMocks
    private SyncController sut = new SyncController();

    @Before
    public void setUp() {
        when(serviceMock.getBearerToken()).thenReturn(bearerToken);
        when(serviceMock.getS2sToken()).thenReturn(s2sToken);


        IdamClient.User user = new IdamClient.User();
        user.setId(idamId);
        user.setForename("April");
        user.setSurname("ONeil");
        user.setEmail("apriloneil@hmcts.net");
        user.setActive(true);
        user.setRoles(MockDataProvider.defaultRoles);
        //user.setRoles(ArrayUtils.toUnmodifiableList(new String[]{"pui-user-manager"}));
        user.setLastModified("2019-07-01T08:16:38.917Z");

        List<IdamClient.User> users = new ArrayList<>();
        users.add(user);
        when(serviceMock.getSyncFeed(ProfileSyncService.BEARER + bearerToken, searchQuery)).thenReturn(users);

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getBearerToken() {
        assertThat(sut.getBearerToken()).isEqualTo(bearerToken);
    }

    @Test
    public void getS2sToken() {
        assertThat(sut.getS2sToken()).isEqualTo(s2sToken);
    }

    @Test
    public void getSyncFeed() {
        assertThat(sut.getSyncFeed(searchQuery)).isEqualTo(feed);
    }

    @Test
    public void updateFeed() {
        assertThat(sut.updateFeed(searchQuery)).isEqualTo(updateFeedAck);
    }
}