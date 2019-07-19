package uk.gov.hmcts.reform.profilesync.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Getter
@Configuration
@ConfigurationProperties(prefix = "idam.api")
@Setter
public class TokenConfigProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;

    private String authorization;
    private String clientAuthorization;
}
