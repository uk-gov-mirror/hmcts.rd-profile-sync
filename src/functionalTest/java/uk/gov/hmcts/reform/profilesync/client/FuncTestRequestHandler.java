package uk.gov.hmcts.reform.profilesync.client;

import io.restassured.RestAssured;

import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.profilesync.config.TestConfigProperties;

@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
@ContextConfiguration(classes = {TestConfigProperties.class})
@TestPropertySource("classpath:application-functional.yaml")
public class FuncTestRequestHandler {

    @Autowired
    protected TestConfigProperties testConfig;

    @Value("${targetInstance}")
    protected String baseUrl;

    @Value("${s2s.auth.secret}")
    protected String s2sSecret;

    @Value("${s2s.auth.url}")
    protected String s2sBaseUrl;

    @Value("${idam.api.url}")
    protected String idamApiUrl;

    @Value("${s2s.auth.microservice:rd_user_profile_api}")
    protected String s2sMicroservice;

    public static final String BEARER = "Bearer ";


    @Before
    public void setupProxy() {
        //TO enable for local testing
        RestAssured.proxy("proxyout.reform.hmcts.net",8080);
        SerenityRest.proxy("proxyout.reform.hmcts.net", 8080);
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
