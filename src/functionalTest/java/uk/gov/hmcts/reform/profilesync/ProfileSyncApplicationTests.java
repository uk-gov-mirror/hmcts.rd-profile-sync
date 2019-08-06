package uk.gov.hmcts.reform.profilesync;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import net.serenitybdd.junit.spring.integration.SpringIntegrationSerenityRunner;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.hmcts.reform.profilesync.client.FuncTestRequestHandler;


@Slf4j
@RunWith(SpringIntegrationSerenityRunner.class)
@ActiveProfiles("functional")
public class ProfileSyncApplicationTests extends FuncTestRequestHandler {

    @Test
    public void testIdamProfileSyncInUserProfile() {

        createUserProfile();

    }

    private void createUserProfile() {

        Map<String, Object> tokenParams = new HashMap<>();
        tokenParams.put("email", buildRandomEmail());
        tokenParams.put("firstName",  RandomStringUtils.randomAlphabetic(20));
        tokenParams.put("lastName",  RandomStringUtils.randomAlphabetic(20));
        tokenParams.put("languagePreference", "EN");
        tokenParams.put("emailCommsConsent", "true");
        tokenParams.put("postalCommsConsent", "true");
        tokenParams.put("userCategory", "PROFESSIONAL");
        tokenParams.put("userType", "EXTERNAL");
        tokenParams.put("roles", getIdamRolesJson());

        Response response = RestAssured
                .given()
                .relaxedHTTPSValidation()
                .baseUri("http://rd-user-profile-api-preview.service.core-compute-preview.internal")
                .header(CONTENT_TYPE, APPLICATION_JSON_UTF8_VALUE)
                .header("ServiceAuthorization",getS2sToken())
                .body(tokenParams)
                .post("/v1/userprofile")
                .andReturn();
        assertThat(response).isNotNull();
    }

    private static String buildRandomEmail() {
        return RandomStringUtils.randomAlphanumeric(20) + "@somewhere.com".toLowerCase();
    }

    public List<String> getIdamRolesJson() {
        List<String> roles = new ArrayList<String>();
        roles.add("pui-user-manager");
        roles.add("pui-organisation-manager");
        roles.add("pui-finance-manager");
        roles.add("pui-case-manager");
        return roles;
    }

}
