package uk.gov.hmcts.reform.profilesync.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import feign.Headers;
import feign.Response;
import lombok.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.gov.hmcts.reform.profilesync.domain.UserProfile;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "userProfileClient", url="${userprofile.api.url}")
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
