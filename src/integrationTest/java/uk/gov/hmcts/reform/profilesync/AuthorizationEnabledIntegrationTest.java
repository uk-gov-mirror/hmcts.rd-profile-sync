package uk.gov.hmcts.reform.profilesync;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import uk.gov.hmcts.reform.profilesync.client.IdamClient;
import uk.gov.hmcts.reform.profilesync.client.UserProfileClient;
import uk.gov.hmcts.reform.profilesync.domain.IdamStatus;
import uk.gov.hmcts.reform.profilesync.repository.SyncJobRepository;
import uk.gov.hmcts.reform.profilesync.util.UserProfileSyncJobScheduler;

@Configuration
@TestPropertySource(properties = {"S2S_URL=http://127.0.0.1:8990","IDAM_URL:http://127.0.0.1:5000", "USER_PROFILE_URL:http://127.0.0.1:8091"})
public abstract class AuthorizationEnabledIntegrationTest  extends SpringBootIntegrationTest{

    @Autowired
    protected SyncJobRepository organisationRepository;

    @Autowired
    protected UserProfileClient userProfileFeignClient;

    @Autowired
    protected IdamClient iDamFeignClient;

    @Autowired
    UserProfileSyncJobScheduler profileSyncJobScheduler ;

    //@Autowired
    protected ProfileSyncApplicationTest profileSyncApplicationTest;

    @Rule
    public WireMockRule s2sService = new WireMockRule(8990);

    @Rule
    public WireMockRule sidamService = new WireMockRule(5000);

    @Rule
    public WireMockRule userProfileService = new WireMockRule(8091);


    @Before
    public void setUpClient() {
        profileSyncApplicationTest = new ProfileSyncApplicationTest();
    }

    @Before
    public void setupS2sAndIdamStubs() throws Exception {

        s2sService.stubFor(get(urlEqualTo("/details"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("it")));

        sidamService.stubFor(get(urlEqualTo("/details"))
                .withHeader("Authorization", equalTo("Bearer eyJ0eXAiOiJKV1QiLCJ6aXAiOiJOT05FIiwia2lkIjoiYi9PNk92VnYxK3krV2dySDVVaTlXVGlvTHQwPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJzdXBlci51c2VyQGhtY3RzLm5ldCIsImF1dGhfbGV2ZWwiOjAsImF1ZGl0VHJhY2tpbmdJZCI6IjZiYTdkYTk4LTRjMGYtNDVmNy04ZjFmLWU2N2NlYjllOGI1OCIsImlzcyI6Imh0dHA6Ly9mci1hbTo4MDgwL29wZW5hbS9vYXV0aDIvaG1jdHMiLCJ0b2tlbk5hbWUiOiJhY2Nlc3NfdG9rZW4iLCJ0b2tlbl90eXBlIjoiQmVhcmVyIiwiYXV0aEdyYW50SWQiOiI0NjAzYjVhYS00Y2ZhLTRhNDQtYWQzZC02ZWI0OTI2YjgxNzYiLCJhdWQiOiJteV9yZWZlcmVuY2VfZGF0YV9jbGllbnRfaWQiLCJuYmYiOjE1NTk4OTgxNzMsImdyYW50X3R5cGUiOiJhdXRob3JpemF0aW9uX2NvZGUiLCJzY29wZSI6WyJhY3IiLCJvcGVuaWQiLCJwcm9maWxlIiwicm9sZXMiLCJjcmVhdGUtdXNlciIsImF1dGhvcml0aWVzIl0sImF1dGhfdGltZSI6MTU1OTg5ODEzNTAwMCwicmVhbG0iOiIvaG1jdHMiLCJleHAiOjE1NTk5MjY5NzMsImlhdCI6MTU1OTg5ODE3MywiZXhwaXJlc19pbiI6Mjg4MDAsImp0aSI6IjgxN2ExNjE0LTVjNzAtNGY4YS05OTI3LWVlYjFlYzJmYWU4NiJ9.RLJyLEKldHeVhQEfSXHhfOpsD_b8dEBff7h0P4nZVLVNzVkNoiPdXYJwBTSUrXl4pyYJXEhdBwkInGp3OfWQKhHcp73_uE6ZXD0eIDZRvCn1Nvi9FZRyRMFQWl1l3Dkn2LxLMq8COh1w4lFfd08aj-VdXZa5xFqQefBeiG_xXBxWkJ-nZcW3tTXU0gUzarGY0xMsFTtyRRilpcup0XwVYhs79xytfbq0WklaMJ-DBTD0gux97KiWBrM8t6_5PUfMDBiMvxKfRNtwGD8gN8Vct9JUgVTj9DAIwg0KPPm1rEETRPszYI2wWvD2lpH2AwUtLBlRDANIkN9SdfiHSETvoQ"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{"
                                + "  \"id\": \"ef4fac86-d3e8-47b6-88a7-c7477fb69d3f\","
                                + "  \"forename\": \"Super\","
                                + "  \"surname\": \"User\","
                                + "  \"email\": \"super.user@hmcts.net\","
                                + "  \"accountStatus\": \"active\","
                                + "  \"roles\": ["
                                + "  \"pui-case-manager\""
                                + "  ]"
                                + "}")));

        sidamService.stubFor(get(urlEqualTo("/api/v1/users"))
               // .withHeader("Authorization", equalTo("Bearer eyJ0eXAiOiJKV1QiLCJ6aXAiOiJOT05FIiwia2lkIjoiYi9PNk92VnYxK3krV2dySDVVaTlXVGlvTHQwPSIsImFsZyI6IlJTMjU2In0.eyJzdWIiOiJzdXBlci51c2VyQGhtY3RzLm5ldCIsImF1dGhfbGV2ZWwiOjAsImF1ZGl0VHJhY2tpbmdJZCI6IjZiYTdkYTk4LTRjMGYtNDVmNy04ZjFmLWU2N2NlYjllOGI1OCIsImlzcyI6Imh0dHA6Ly9mci1hbTo4MDgwL29wZW5hbS9vYXV0aDIvaG1jdHMiLCJ0b2tlbk5hbWUiOiJhY2Nlc3NfdG9rZW4iLCJ0b2tlbl90eXBlIjoiQmVhcmVyIiwiYXV0aEdyYW50SWQiOiI0NjAzYjVhYS00Y2ZhLTRhNDQtYWQzZC02ZWI0OTI2YjgxNzYiLCJhdWQiOiJteV9yZWZlcmVuY2VfZGF0YV9jbGllbnRfaWQiLCJuYmYiOjE1NTk4OTgxNzMsImdyYW50X3R5cGUiOiJhdXRob3JpemF0aW9uX2NvZGUiLCJzY29wZSI6WyJhY3IiLCJvcGVuaWQiLCJwcm9maWxlIiwicm9sZXMiLCJjcmVhdGUtdXNlciIsImF1dGhvcml0aWVzIl0sImF1dGhfdGltZSI6MTU1OTg5ODEzNTAwMCwicmVhbG0iOiIvaG1jdHMiLCJleHAiOjE1NTk5MjY5NzMsImlhdCI6MTU1OTg5ODE3MywiZXhwaXJlc19pbiI6Mjg4MDAsImp0aSI6IjgxN2ExNjE0LTVjNzAtNGY4YS05OTI3LWVlYjFlYzJmYWU4NiJ9.RLJyLEKldHeVhQEfSXHhfOpsD_b8dEBff7h0P4nZVLVNzVkNoiPdXYJwBTSUrXl4pyYJXEhdBwkInGp3OfWQKhHcp73_uE6ZXD0eIDZRvCn1Nvi9FZRyRMFQWl1l3Dkn2LxLMq8COh1w4lFfd08aj-VdXZa5xFqQefBeiG_xXBxWkJ-nZcW3tTXU0gUzarGY0xMsFTtyRRilpcup0XwVYhs79xytfbq0WklaMJ-DBTD0gux97KiWBrM8t6_5PUfMDBiMvxKfRNtwGD8gN8Vct9JUgVTj9DAIwg0KPPm1rEETRPszYI2wWvD2lpH2AwUtLBlRDANIkN9SdfiHSETvoQ"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{"
                                + "  \"id\": \"ef4fac86-d3e8-47b6-88a7-c7477fb69d3f\","
                                + "  \"forename\": \"Super\","
                                + "  \"surname\": \"User\","
                                + "  \"email\": \"super.user@hmcts.net\","
                                + "  \"accountStatus\": \"active\","
                                + "  \"roles\": ["
                                + "  \"pui-case-manager\""
                                + "  ]"
                                + "}")));

    }



    @Before
    public void userProfileGetUserWireMock() {

        userProfileService.stubFor(WireMock.get(urlPathMatching("/v1/userprofile.*"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)
                        .withBody("{"
                                + "  \"userIdentifier\":\"ef4fac86-d3e8-47b6-88a7-c7477fb69d3f\","
                                + "  \"firstName\": \"prashanth\","
                                + "  \"lastName\": \"rao\","
                                + "  \"email\": \"super.user@hmcts.net\","
                                + "  \"idamStatus\": \"" + IdamStatus.ACTIVE + "\""
                                + "}")));
    }

    @Before
    public void userProfileSyncWireMock() {

        userProfileService.stubFor(WireMock.put(urlPathMatching("/v1/userprofile/*"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withStatus(200)));

    }

    @After
    public void cleanupTestData() {


    }

    public void userProfileCreateUserWireMock(HttpStatus status) {
        String body = null;
        int returnHttpStaus = status.value();
        if (status.is2xxSuccessful()) {
            body = "{"
                    + "  \"idamId\":\"ef4fac86-d3e8-47b6-88a7-c7477fb69d3f\","
                    + "  \"idamRegistrationResponse\":\"201\""
                    + "}";
            returnHttpStaus = 201;
        }

        userProfileService.stubFor(
                WireMock.post(urlPathMatching("/v1/userprofile"))
                        .willReturn(
                                aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(body)
                                        .withStatus(200)
                        )
        );
    }
}

