package uk.gov.hmcts.reform.profilesync.service.impl;

import feign.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.hmcts.reform.authorisation.generators.AuthTokenGenerator;
import uk.gov.hmcts.reform.profilesync.client.IdamClient;
import uk.gov.hmcts.reform.profilesync.config.TokenConfigProperties;
import uk.gov.hmcts.reform.profilesync.domain.ErrorResponse;
import uk.gov.hmcts.reform.profilesync.domain.Source;
import uk.gov.hmcts.reform.profilesync.domain.SyncJobAudit;
import uk.gov.hmcts.reform.profilesync.domain.User;
import uk.gov.hmcts.reform.profilesync.repository.SyncJobRepository;
import uk.gov.hmcts.reform.profilesync.service.ProfileSyncService;
import uk.gov.hmcts.reform.profilesync.service.ProfileUpdateService;
import uk.gov.hmcts.reform.profilesync.util.JsonFeignResponseHelper;

@Service
@AllArgsConstructor
@Slf4j
public class ProfileSyncServiceImpl implements ProfileSyncService {

    @Autowired
    protected final IdamClient idamClient;

    @Autowired
    protected final AuthTokenGenerator tokenGenerator;

    @Autowired
    protected final ProfileUpdateService profileUpdateService;

    @Autowired
    private final TokenConfigProperties props;

    @Autowired
    private final SyncJobRepository syncJobRepository;

    public String authorize() {

        Map<String, String> formParams = new HashMap<>();
        formParams.put("client_id", props.getClientId());
        formParams.put("redirect_uri", props.getRedirectUri());
        formParams.put("response_type", "code");
        formParams.put("scope", "openid profile roles create-user manage-user search-user");

        IdamClient.AuthenticateUserResponse response = idamClient.authorize(BASIC + props.getAuthorization(), formParams, "");

        return response.getCode();
    }

    public String getBearerToken() {

        Map<String, String> formParams = new HashMap<>();
        formParams.put("client_id", props.getClientId());
        formParams.put("redirect_uri", props.getRedirectUri());
        formParams.put("code", authorize());
        formParams.put("grant_type", "authorization_code");

        IdamClient.TokenExchangeResponse response = idamClient.getToken(BASIC + props.getClientAuthorization(), formParams, "");

        return response.getAccessToken();
    }

    public String getS2sToken() {
        return tokenGenerator.generate();
    }

    public List<IdamClient.User> getSyncFeed(String bearerToken, String searchQuery) {
        log.info("Inside getSyncFeed");
        List<IdamClient.User> totalUsers = new ArrayList<>();
        List<User> users = new ArrayList<>();

        //Users user = null;

        Map<String, String> formParams = new HashMap<>();
        formParams.put("query", searchQuery);
        Response response  = idamClient.getUserFeed(bearerToken, formParams);
        ResponseEntity responseEntity = JsonFeignResponseHelper.toResponseEntity(response, IdamClient.User.class);
        Class clazz = response.status() > 300 ? ErrorResponse.class : IdamClient.User.class;

        if ( response.status() < 300 && responseEntity.getStatusCode().is2xxSuccessful()) {

            int records = Integer.parseInt(responseEntity.getHeaders().get("X-Total-Count").get(0));
            if (records > 20) {

                List<String> totalRecords = new ArrayList<>();
                // users = (Users) response.body();
                for (Integer i : numberOfCallsToIdamService(records)) {

                    callToGetUserFeed(bearerToken,i,formParams,totalUsers);

                }

                log.info("size of the Users array::" + users.size());
            } else {

             //   totalUsers =  (List<IdamClient.User>) response.body();

            }

          //  log.info("Found record in User Profile with idamId = {}");
        }

        if(CollectionUtils.isEmpty(totalUsers)) {
            SyncJobAudit syncJobAudit = new SyncJobAudit(200, "success", Source.SYNC);
            syncJobRepository.save(syncJobAudit);
        }
        return totalUsers;
    }

    private void callToGetUserFeed(String token, Integer pageNo, Map<String, String> formParams,List<IdamClient.User> usersForAllPages) {
        String newQuery = "roles:\"pui-case-manager\" OR roles:\"pui-user-manager\" OR roles:\"pui-organisation-manager\" OR roles:\"pui-finance-manager\"$PAGE$";
        String dynamicQuery = newQuery.replace("$PAGE$","&page="+pageNo);
        log.info("dynamicQuery::" + dynamicQuery);
        formParams.put("query",dynamicQuery);
        List<IdamClient.User> usersPerPage = null;
        Response response  = idamClient.getUserFeed(token, formParams);
        ResponseEntity responseEntity = JsonFeignResponseHelper.toResponseEntity(response, IdamClient.User.class);
        Class clazz = response.status() > 300 ? ErrorResponse.class : IdamClient.User.class;

        if ( response.status() < 300 && responseEntity.getStatusCode().is2xxSuccessful()) {

             // usersPerPage = (IdamClient.User) responseEntity.getBody();
             IdamClient.User users = (IdamClient.User) responseEntity.getBody();
             log.info("User ::" + users);
              //usersForAllPages.addAll(usersPerPage);
        }

    }


   /* public List<String>  decode(Response response) throws IOException, FeignException {
        byte[] body = null;
        List<String> result = null;
        if (null != response) {

            //body = response.body() != null ? Util.toByteArray(response.body().asInputStream()) : new byte[0];
            Reader reader = null;
            try {
                reader = response.body().asReader();
                //Easy way to read the stream and get a String object
                result = CharStreams.readLines(reader);
                //use a Jackson ObjectMapper to convert the Json String into a
                //Pojo
                ObjectMapper mapper = new ObjectMapper();
                //just in case you missed an attribute in the Pojo
                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            } catch(Exception e) {

            }

        }
        return result;
    }*/


    private List<Integer> numberOfCallsToIdamService(int totalRecords) {

        List<Integer> pages =  new ArrayList<>();
        int pageCount = totalRecords/20;
        int index = 0;
        if (totalRecords%20 > 0) {
            pageCount = pageCount + 1;
        }
        while(index<pageCount) {
            pages.add(index);
            index ++;
        }
        return pages;
    }

    public void updateUserProfileFeed(String searchQuery, int recordsCount) {
        log.info("Inside updateUserProfileFeed");
        String bearerToken = BEARER + getBearerToken();
        getSyncFeed(bearerToken, searchQuery);
        // profileUpdateService.updateUserProfile(searchQuery, bearerToken, getS2sToken(), getSyncFeed(bearerToken, searchQuery), recordsCount);
        log.info("After updateUserProfileFeed");
    }
}
