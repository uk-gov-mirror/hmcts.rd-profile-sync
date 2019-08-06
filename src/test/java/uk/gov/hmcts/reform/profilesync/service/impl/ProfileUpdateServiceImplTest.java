package uk.gov.hmcts.reform.profilesync.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.profilesync.client.IdamClient;
import uk.gov.hmcts.reform.profilesync.client.UserProfileClient;
import uk.gov.hmcts.reform.profilesync.domain.GetUserProfileResponse;
import uk.gov.hmcts.reform.profilesync.domain.UserProfile;
import uk.gov.hmcts.reform.profilesync.helper.MockDataProvider;
import uk.gov.hmcts.reform.profilesync.repository.SyncJobRepository;
import uk.gov.hmcts.reform.profilesync.service.ProfileUpdateService;
import uk.gov.hmcts.reform.profilesync.service.UserAcquisitionService;

public class ProfileUpdateServiceImplTest {

    private final UserProfileClient userProfileClientMock = Mockito.mock(UserProfileClient.class);

    private final UserAcquisitionService userAcquisitionServiceMock = Mockito.mock(UserAcquisitionService.class);

    private final SyncJobRepository syncJobRepositoryMock = Mockito.mock(SyncJobRepository.class);
    private final ProfileUpdateService sut = new ProfileUpdateServiceImpl(userAcquisitionServiceMock,userProfileClientMock,syncJobRepositoryMock);

    private final AuthTokenGenerator tokenGeneratorMock = Mockito.mock(AuthTokenGenerator.class);

    @Test
    public void testUpdateUserProfile() throws Exception {
        final String searchQuery = "lastModified:>now-24h";
        final String bearerToken = "foobar";
        final String s2sToken = "ey0somes2stoken";
        final List<IdamClient.User> users = new ArrayList<>();

        users.add(MockDataProvider.getIdamUser());
        users.add(MockDataProvider.getIdamUser());

        UserProfile userProfile = MockDataProvider.getUserProfile();

        final GetUserProfileResponse getUserProfileResponse = new GetUserProfileResponse(userProfile);

        Optional<GetUserProfileResponse> userProfileOpt = Optional.ofNullable(getUserProfileResponse);

        when(userAcquisitionServiceMock.findUser(any(String.class), any(String.class), any(String.class))).thenReturn(userProfileOpt);
        when(tokenGeneratorMock.generate()).thenReturn(s2sToken);

        sut.updateUserProfile(searchQuery, bearerToken, s2sToken, users);


        verify(userAcquisitionServiceMock, times(2)).findUser(eq(bearerToken), eq(s2sToken), eq(MockDataProvider.idamId.toString()));


    }
}