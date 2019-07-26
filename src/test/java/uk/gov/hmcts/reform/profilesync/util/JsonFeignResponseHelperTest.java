package uk.gov.hmcts.reform.profilesync.util;

import feign.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import uk.gov.hmcts.reform.profilesync.domain.CreateUserProfileResponse;
import uk.gov.hmcts.reform.profilesync.helper.MockDataProvider;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.when;

public class JsonFeignResponseHelperTest {
    final int statusCode = 200;
    private Response responseMock;

    @Before
    public void setUp() {
        responseMock = Mockito.mock(Response.class);
        Reader readerMock = Mockito.mock(Reader.class);
        Response.Body bodyMock = Mockito.mock(Response.Body.class);

        try {
            when(responseMock.body()).thenReturn(bodyMock);
            when(responseMock.body().asReader()).thenReturn(readerMock);
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Exception ex:" + ex);
        }

        when(responseMock.status()).thenReturn(statusCode);
    }

    @Test
    public void testDecode() {
        JsonFeignResponseHelper.decode(responseMock, CreateUserProfileResponse.class);

        ResponseEntity entity = JsonFeignResponseHelper.toResponseEntity(this.responseMock, CreateUserProfileResponse.class);

        assertThat(entity).isNotNull();
    }

    @Test
    public void testToResponseEntity() {
        ResponseEntity actual = JsonFeignResponseHelper.toResponseEntity(this.responseMock, CreateUserProfileResponse.class);

        assertThat(actual).isNotNull();
    }

    @Test
    public void testConvertHeaders() {
        Map<String, Collection<String>> data = new HashMap<>();

        Collection<String> list = new ArrayList<>(Arrays.asList(new String[]{"Authorization",MockDataProvider.authorization}));

        data.put("MyHttpData", list);

        MultiValueMap<String, String> actual = JsonFeignResponseHelper.convertHeaders(data);

        assertThat(actual).isNotNull();
        assertThat(actual.get("MyHttpData")).isNotNull();
    }
}