package uk.gov.hmcts.reform.profilesync;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import uk.gov.hmcts.reform.profilesync.client.IdamClient;
import uk.gov.hmcts.reform.profilesync.config.TestConfigProperties;

import java.util.HashMap;
import java.util.Map;

@RunWith(SpringIntegrationSerenityRunner.class)
public class CreateUserProfileFuncTest extends AbstractFunctional {

    @Autowired
    protected TestConfigProperties configProperties;

    private IdamClient idamClient;

    @Before
    public void setUp() {
        RestAssured.baseURI = targetInstance;
        RestAssured.useRelaxedHTTPSValidation();
        idamClient = new IdamClient(configProperties);
    }

    @Test
    public void should_create_user_profile_and_verify_successfully() throws Exception {

        /*CreateUserProfileResponse createdResource = createUserProfile(createUserProfileData(), HttpStatus.CREATED);
        assertThat(createdResource).isNotNull();
        assertThat(createdResource.getIdamId()).isNotNull();*/


    }

}
