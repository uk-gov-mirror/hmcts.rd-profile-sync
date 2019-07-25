package uk.gov.hmcts.reform.profilesync.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class UserProfile {

    private Long id;
    private UUID idamId;
    private String email;
    private String firstName;
    private String lastName;

    private boolean emailCommsConsent;
    private LocalDateTime emailCommsConsentTs;
    private boolean postalCommsConsent;
    private LocalDateTime postalCommsConsentTs;

    private String status;
    private Integer idamRegistrationResponse;

    private LocalDateTime created;

    private LocalDateTime lastUpdated;

}
