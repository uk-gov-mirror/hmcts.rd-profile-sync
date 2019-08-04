package uk.gov.hmcts.reform.profilesync.client;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.rest.SerenityRest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.profilesync.config.TestConfigProperties;

@Slf4j
@Service
public class FuncTestRequestHandler {

    @Autowired
    protected TestConfigProperties testConfig;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${targetInstance}")
    protected String baseUrl;

    @Value("${s2s.auth.secret}")
    protected String s2sSecret;

    @Value("${s2s.auth.url}")
    protected String s2sBaseUrl;

    @Value("${s2s.auth.microservice:rd_user_profile_api}")
    protected String s2sMicroservice;

    public static final String BEARER = "Bearer ";

    private RequestSpecification withAuthenticatedRequest() {
        String s2sToken = getS2sToken();
        String bearerToken = getBearerToken();

        log.info("S2S Token : {}, Bearer Token : {}", s2sToken, bearerToken);

        return SerenityRest.given()
                .relaxedHTTPSValidation()
                .baseUri(baseUrl)
                .header("ServiceAuthorization", BEARER + s2sToken)
                .header("Authorization", BEARER + bearerToken)
                .header("Content-Type", APPLICATION_JSON_UTF8_VALUE)
                .header("Accepts", APPLICATION_JSON_UTF8_VALUE);
    }

    public String getBearerToken() {
        IdamClient idamClient = new IdamClient(testConfig);
        return idamClient.getBearerToken();
    }

    public String getS2sToken() {
        log.info("S2s Base url : {}, Microservice : {}, Secret : {}", s2sBaseUrl, s2sMicroservice, s2sSecret);
        S2sClient client = new S2sClient(s2sBaseUrl, s2sMicroservice, s2sSecret);
        return client.getS2sToken();
    }

}
