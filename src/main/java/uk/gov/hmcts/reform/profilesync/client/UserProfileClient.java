package uk.gov.hmcts.reform.profilesync.client;

import feign.Headers;
import feign.Response;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import uk.gov.hmcts.reform.profilesync.domain.UserProfile;

@FeignClient(name = "userProfileClient", url = "${userprofile.api.url}")
public interface UserProfileClient {

    @PutMapping(value = "/v1/userprofile/{userId}", consumes = {MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Headers({"authorization: {authorization}", "serviceauthorization: {serviceauthorization}"})
    public Response syncUserStatus(@RequestHeader("authorization") String authorization,
                                   @RequestHeader("serviceauthorization") String serviceAuthorization,
                                   @PathVariable("userId") String userId, @RequestBody UserProfile body);


    @GetMapping(value = "/v1/userprofile", produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Headers({"authorization: {authorization}", "serviceauthorization: {serviceauthorization}"})
    public Response findUser(@RequestHeader("authorization") String authorization,
                                   @RequestHeader("serviceauthorization") String serviceAuthorization,
                                   @RequestParam("userId") String userId);

}
