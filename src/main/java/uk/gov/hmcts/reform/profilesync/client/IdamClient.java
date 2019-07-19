package uk.gov.hmcts.reform.profilesync.client;


import com.fasterxml.jackson.annotation.JsonProperty;
import feign.*;
import lombok.Data;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "idamClient", url="${idam.api.url}")
public interface IdamClient {

    @PostMapping(value = "/oauth2/authorize", consumes = {"application/x-www-form-urlencoded"})
    @Headers("authorization: {authorization}")
    public AuthenticateUserResponse authorize(@RequestHeader("authorization") String authorize, @RequestParam  Map<String, String> params, String body);

    @PostMapping(value = "/oauth2/token", consumes = {"application/x-www-form-urlencoded"})
    @Headers("authorization: {authorization}")
    public TokenExchangeResponse getToken(@RequestHeader("authorization") String authorize, @RequestParam  Map<String, String> params, String body);

    @GetMapping(value = "/api/v1/users", consumes = {"application/x-www-form-urlencoded"})
    @Headers("authorization: {authorization}")
    public List<User> getUserFeed(@RequestHeader("authorization") String authorization, @RequestParam  Map<String, String> params);


    @Data
    class AuthenticateUserResponse {
        @JsonProperty("code")
        private String code;
    }

    @Data
    class TokenExchangeResponse {
        @JsonProperty("access_token")
        private String accessToken;
    }


    @Data
    class User {
        @JsonProperty("active")
        private boolean active;

        @JsonProperty("email")
        private String email;

        @JsonProperty("forename")
        private String forename;

        @JsonProperty("id")
        private UUID id;

        @JsonProperty("lastModified")
        private String lastModified;

        @JsonProperty("locked")
        private boolean locked;

        @JsonProperty("pending")
        private boolean pending;

        @JsonProperty("roles")
        private List<String> roles;

        @JsonProperty("surname")
        private String surname;
    }

}
