package uk.gov.hmcts.reform.profilesync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lombok.Setter;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.hmcts.reform.profilesync.client.FuncTestRequestHandler;
import uk.gov.hmcts.reform.profilesync.config.TestConfigProperties;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;

@Setter
public class CreateUserProfileDataTestBuilder extends FuncTestRequestHandler {

    private CreateUserProfileDataTestBuilder() {
        //not meant to be instantiated.
    }

    public  void buildCreateUserProfileData() {

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
                .header(CONTENT_TYPE, APPLICATION_FORM_URLENCODED_VALUE)
                .header("Authorization",getBearerToken())
                .header("ServiceAuthorization",getS2sToken())
                .body(tokenParams)
                .post("/v1/userprofile")
                .andReturn();

    }

    private static String buildRandomEmail() {
        return RandomStringUtils.randomAlphanumeric(20) + "@somewhere.com".toLowerCase();
    }

    public  List<String> getIdamRolesJson() {
        List<String> roles = new ArrayList<String>();
        roles.add("pui-user-manager");
        roles.add("pui-organisation-manager");
        roles.add("pui-finance-manager");
        roles.add("pui-case-manager");
        return roles;
    }

}
