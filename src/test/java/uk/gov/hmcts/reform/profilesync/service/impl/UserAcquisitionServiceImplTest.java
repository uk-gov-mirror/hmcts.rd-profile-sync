package uk.gov.hmcts.reform.profilesync.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.hmcts.reform.profilesync.client.UserProfileClient;
import uk.gov.hmcts.reform.profilesync.domain.GetUserProfileResponse;
import uk.gov.hmcts.reform.profilesync.helper.MockDataProvider;
import uk.gov.hmcts.reform.profilesync.service.UserAcquisitionService;

import java.io.IOException;
import java.io.Reader;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserAcquisitionServiceImplTest {

    private UserProfileClient userProfileClientMock = Mockito.mock(UserProfileClient.class);

    private UserAcquisitionService sut = new UserAcquisitionServiceImpl(userProfileClientMock);

    @Test
    public void testFindUser() throws IOException {
        int statusCode = 200;
        String bearerToken = "Bearer ey093089r0e90e9f0jj9w00w-f90fsj0sf-fji0fsejs0";
        String s2sToken = "ey0f90sjaf90adjf90asjfsdljfklsf0sfj9s0d";
        String id = MockDataProvider.idamId.toString();


        Optional<GetUserProfileResponse> findUser = Optional.of(MockDataProvider.getGetUserProfileResponse());

        Response responseMock = Mockito.mock(Response.class);
        Reader readerMock = Mockito.mock(Reader.class);
        final ObjectMapper json = new ObjectMapper();// TODO need to inject this in service code
        Response.Body bodyMock = Mockito.mock(Response.Body.class);

        when(responseMock.status()).thenReturn(statusCode);
        when(responseMock.body()).thenReturn(bodyMock);
        when(responseMock.body().asReader()).thenReturn(readerMock);
        when(userProfileClientMock.findUser(any(), any(), any())).thenReturn(responseMock);

        Optional<GetUserProfileResponse> getUserProfileResponse = sut.findUser(bearerToken, s2sToken, id);

        assertThat(getUserProfileResponse).isNotNull();

    }
}