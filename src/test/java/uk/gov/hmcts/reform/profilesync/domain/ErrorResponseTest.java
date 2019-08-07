package uk.gov.hmcts.reform.profilesync.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class ErrorResponseTest {

    ErrorResponse errorResponseTest = new ErrorResponse();

    @Test
    public void testErrorResponseTest() {

        ErrorResponse errorResponseTest1 = new ErrorResponse("errorMessage", "errorDescription", "timeStamp");

        assertThat(errorResponseTest1.getErrorMessage()).isEqualTo("errorMessage");
        assertThat(errorResponseTest1.getErrorDescription()).isEqualTo("errorDescription");
        assertThat(errorResponseTest1.getTimeStamp()).isEqualTo("timeStamp");

    }

}