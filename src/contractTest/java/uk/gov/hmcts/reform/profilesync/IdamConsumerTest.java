package uk.gov.hmcts.reform.profilesync;

import static org.assertj.core.api.Assertions.assertThat;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslJsonBody;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.model.RequestResponsePact;
import com.google.common.collect.Maps;
import groovy.util.logging.Slf4j;
import io.restassured.http.ContentType;

import java.util.Map;
import java.util.TreeMap;

import net.serenitybdd.rest.SerenityRest;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@Slf4j
@ExtendWith(PactConsumerTestExt.class)
@ExtendWith(SpringExtension.class)
public class IdamConsumerTest {

    private static final String IDAM_OPEN_ID_TOKEN_URL = "/o/token";
    private static final String IDAM_GET_USER_URL = "/api/v1/users";
    private static final String ACCESS_TOKEN = "Bearer eyJ0eXAiOiJKV1QiLCJ6aXAiOiJOT05FIiwia2lkIjoiMWVyMFdSd2dJT1RBRm9qRTRyQy9mYmVLdTNJPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJhZG1pbi5yZWZkYXRhQGhtY3RzLm5ldCIsImN0cyI6Ik9BVVRIMl9TVEFURUxFU1NfR1JBTlQiLCJhdXRoX2xldmVsIjowLCJhdWRpdFRyYWNraW5nSWQiOiJhYjhlM2VlNi02ZjE1LTQ1MjItOTQzNC0yYzY0ZGJmZDcwYzAtMTEwMDY3NzUiLCJpc3MiOiJodHRwczovL2Zvcmdlcm9jay1hbS5zZXJ2aWNlLmNvcmUtY29tcHV0ZS1pZGFtLWFhdDIuaW50ZXJuYWw6ODQ0My9vcGVuYW0vb2F1dGgyL2htY3RzIiwidG9rZW5OYW1lIjoiYWNjZXNzX3Rva2VuIiwidG9rZW5fdHlwZSI6IkJlYXJlciIsImF1dGhHcmFudElkIjoiSkVXSW1DejlZSHUybk9SNmF1SHJnWWdNS2dzIiwiYXVkIjoicmQtcHJvZmVzc2lvbmFsLWFwaSIsIm5iZiI6MTU5MjkwMzI4NCwiZ3JhbnRfdHlwZSI6InBhc3N3b3JkIiwic2NvcGUiOlsib3BlbmlkIiwicHJvZmlsZSIsInJvbGVzIiwiY3JlYXRlLXVzZXIiLCJtYW5hZ2UtdXNlciIsInNlYXJjaC11c2VyIl0sImF1dGhfdGltZSI6MTU5MjkwMzI4NCwicmVhbG0iOiIvaG1jdHMiLCJleHAiOjE1OTI5MzIwODQsImlhdCI6MTU5MjkwMzI4NCwiZXhwaXJlc19pbiI6Mjg4MDAsImp0aSI6Ii1vQmU1bWhSaGRzX2dqemZFWXlmRzgyNzNDTSJ9.VUAKkB2sPaZAYUEKOhkN5JWtCXKAjHB5Y016eRqGzCXaKWqdm2T6FLTz-51YGfGPh4OW0wGdzCNUW7FUZxOUVnRgkNtI-74f7l_zlLOSO1xGa1xhP0PJ1OZNrCB2jEwO31b6bwLhD_JPaIEIaWu727pU9v1DTrjpBcUSCN1vGeZN0o9iVuPLcMVUYi24YcsjDFhNp9j6S3elqCvZcH8pwhXXUZ2lvCsV9k86gEC-9rwX49Zi2xw_e_xxSHz6KMkjM8_-lPu3RyayiELfQwhjXeGDJJm4adquohWJkL0YXBdgiHmHDGfuaoUVfDQ5R-g4eOtYkAUo8cOePO_PScURig";
    private static final String FORE_NAME = "forename";
    private static final String SUR_NAME = "surname";
    private static final String ROLES = "roles";


    @Pact(provider = "Idam_api", consumer = "rd_profile_sync__idam_api")
    public RequestResponsePact executeGetIdamAccessTokenAndGet200(PactDslWithProvider builder) throws JSONException {
        String[] rolesArray = new String[1];
        rolesArray[0] = "prd-admin";

        Map<String, String> responseHeaders = Maps.newHashMap();
        responseHeaders.put("Content-Type", "application/json");

        Map<String, Object> params = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        params.put("email", "prdadmin@email.net");
        params.put("password", "Password123");
        params.put("forename","profilesyncAdmin");
        params.put("surname", "jar123");
        params.put("roles", rolesArray);

        return builder
                .given("a user exists", params)
                .uponReceiving("Provider takes user/pwd and returns Access Token to RD - PROFILE SYNC API")
                .path(IDAM_OPEN_ID_TOKEN_URL)
                .method(HttpMethod.POST.toString())
                .body("redirect_uri=http%3A%2F%2Fwww.dummy-pact-service.com%2Fcallback&client_id=pact&grant_type=password&username=prdadmin%40email.net&password=Password123&client_secret=pactsecret&scope=openid profile roles manage-user create-user search-user","application/x-www-form-urlencoded")
                .willRespondWith()
                .status(HttpStatus.OK.value())
                .headers(responseHeaders)
                .body(createAuthResponse())
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "executeGetIdamAccessTokenAndGet200")
    public void should_post_to_token_endpoint_and_receive_access_token_with_200_response(MockServer mockServer)
            throws JSONException {

        String actualResponseBody =
                SerenityRest
                        .given()
                        .contentType(ContentType.URLENC)
                        .formParam("redirect_uri", "http://www.dummy-pact-service.com/callback")
                        .formParam("client_id", "pact")
                        .formParam("grant_type", "password")
                        .formParam("username", "prdadmin@email.net")
                        .formParam("password", "Password123")
                        .formParam("client_secret", "pactsecret")
                        .formParam("scope", "openid profile roles manage-user create-user search-user")
                        .post(mockServer.getUrl() + IDAM_OPEN_ID_TOKEN_URL)
                        .then()
                        .log().all().extract().asString();

        JSONObject response = new JSONObject(actualResponseBody);

        assertThat(response).isNotNull();
        assertThat(response.getString("access_token")).isNotBlank();
        assertThat(response.getString("token_type")).isEqualTo("Bearer");
        assertThat(response.getString("expires_in")).isNotBlank();

    }

    @Test
    @Pact(provider = "Idam_api", consumer = "rd_profile_sync__idam_api")
    public RequestResponsePact executeGetUserAndGet200(PactDslWithProvider builder) {

        Map<String, String> headers = Maps.newHashMap();
        headers.put(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN);
        headers.put("Content-Type", "application/json");

        Map<String, String> responseHeaders = Maps.newHashMap();
        responseHeaders.put("Content-Type", "application/json");

        Map<String, Object> formParam = new TreeMap<>();
        formParam.put("redirect_uri", "http://www.dummy-pact-service.com/callback");
        formParam.put("client_id", "pact");
        formParam.put("grant_type", "password");
        formParam.put("username", "prdadmin@email.net");
        formParam.put("password", "Password123");
        formParam.put("client_secret", "pactsecret");
        formParam.put("scope", "openid profile roles manage-user create-user search-user");

        return builder
                .given("I have obtained an access_token as a user", formParam)
                .uponReceiving("Provider receives a GET /api/v1/users request from an RD - PROFILE SYNC API")
                .query("query=(roles:pui-case-manager OR roles:pui-user-manager OR roles:pui-organisation-manager OR roles:pui-finance-manager) AND lastModified:>now-1h")
                .path(IDAM_GET_USER_URL)
                .method(HttpMethod.GET.toString())
                .headers(headers)
                .willRespondWith()
                .status(HttpStatus.OK.value())
                .headers(responseHeaders)
                .body(createUserDetailsResponse())
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "executeGetUserAndGet200")
    public void should_get_user_from_elastic_search(MockServer mockServer) throws JSONException, ParseException {
        //passing hardcoded token which is valid for 8 hours. So every time when you run the test case generate new token and paste in the code.
        Map<String, String> headers = Maps.newHashMap();
        headers.put(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN);
        headers.put("Content-Type", "application/json");

        Map<String, String> responseHeaders = Maps.newHashMap();
        responseHeaders.put("Content-Type", "application/json");

        String actualResponseBody =
                SerenityRest
                        .given()
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .queryParam("query","(roles:pui-case-manager OR roles:pui-user-manager OR roles:pui-organisation-manager OR roles:pui-finance-manager) AND lastModified:>now-1h")
                        .when()
                        .headers(headers)
                       .get(mockServer.getUrl() + IDAM_GET_USER_URL)
                        .then()
                        .statusCode(200)
                        .headers(responseHeaders)
                        .and()
                        .extract()
                        .body()
                        .asString();

        JSONArray response = new JSONArray(actualResponseBody);

        JSONObject jsonobject = response.getJSONObject(0);
        assertThat(jsonobject.getString("id")).isNotBlank();
        assertThat(jsonobject.getString(FORE_NAME)).isNotBlank();
        assertThat(jsonobject.getString(SUR_NAME)).isNotBlank();
        JSONArray rolesArr = new JSONArray(jsonobject.getString(ROLES));
        assertThat(rolesArr).isNotNull();
        assertThat(rolesArr.length()).isNotZero();
        assertThat(rolesArr.get(0).toString()).isNotBlank();

    }


    private DslPart createUserDetailsResponse() {
        boolean status = true;
        PactDslJsonArray array = new PactDslJsonArray()
                .string("pui-organisation-manager")
                .string("pui-case-manager");
        return PactDslJsonArray.arrayEachLike(1)
                .stringType("id", "a833c2e2-2c73-4900-96ca-74b1efb37928")
                .stringType(FORE_NAME, "Jack")
                .stringType(SUR_NAME, "Skellington")
                .stringType("email", "jackS@spookmail.com")
                .booleanType("active", true)
                .stringType(ROLES, array.toString())
                .stringType("lastModified", "2020-06-22T10:52:57.812Z")
                .closeObject();
    }

    private PactDslJsonBody createAuthResponse() {

        return new PactDslJsonBody()
                .stringType("access_token", "eyJ0eXAiOiJKV1QiLCJ6aXAiOiJOT05FI")
                .stringType("refresh_token", "eyJ0eXAiOiJKV1QiLCJ6aXAiOiJOT05FIiwia2lkIjoiYi9PNk92V")
                .stringType("scope", "openid profile roles manage-user create-user search-user")
                .stringType("id_token", "eyJ0eXAiOiJKV1QiLCJraWQiOiJiL082T3ZWdjEre")
                .stringType("token_type", "Bearer")
                .stringType("expires_in","28798");
    }

}