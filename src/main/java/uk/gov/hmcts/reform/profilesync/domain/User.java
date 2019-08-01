package uk.gov.hmcts.reform.profilesync.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.UUID;
import lombok.Data;

@Data
public  class User {
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
