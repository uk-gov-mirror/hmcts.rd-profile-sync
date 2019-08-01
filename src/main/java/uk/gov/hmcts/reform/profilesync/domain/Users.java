package uk.gov.hmcts.reform.profilesync.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class Users {


    List<User> users = new ArrayList<>();
}
