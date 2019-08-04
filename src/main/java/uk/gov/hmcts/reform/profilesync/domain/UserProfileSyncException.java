package uk.gov.hmcts.reform.profilesync.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UserProfileException extends RuntimeException {

    private HttpStatus httpStatus;

    private String errorMessage;

    public UserProfileException(HttpStatus httpStatus, String errorMessage) {
        super(errorMessage);
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
