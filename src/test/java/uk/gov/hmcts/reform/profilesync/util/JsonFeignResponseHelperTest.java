package uk.gov.hmcts.reform.profilesync.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import uk.gov.hmcts.reform.profilesync.domain.CreateUserProfileResponse;

import java.io.Reader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class JsonFeignResponseHelperTest {
    final int statusCode = 200;
    private Response responseMock;

    @Before
    public void setUp() {
        responseMock = Mockito.mock(Response.class);
        Reader readerMock = Mockito.mock(Reader.class);
        final ObjectMapper json = new ObjectMapper();
        Response.Body bodyMock = Mockito.mock(Response.Body.class);

        try {
            when(responseMock.body()).thenReturn(bodyMock);
            when(responseMock.body().asReader()).thenReturn(readerMock);//.thenReturn(reader);
        } catch (Exception ex) {
            ex.printStackTrace();
            //fail("Exception ex:" + ex);
        }

        when(responseMock.status()).thenReturn(statusCode);
    }

    @Test
    public void decode() {
        JsonFeignResponseHelper.decode(responseMock, CreateUserProfileResponse.class);

        ResponseEntity entity = JsonFeignResponseHelper.toResponseEntity(this.responseMock, CreateUserProfileResponse.class);

        assertThat(entity).isNotNull();
    }

    @Test
    public void toResponseEntity() {
        ResponseEntity actual = JsonFeignResponseHelper.toResponseEntity(this.responseMock, CreateUserProfileResponse.class);

        assertThat(actual).isNotNull();
    }

    @Test
    public void convertHeaders() {
    }
}