package uk.gov.hmcts.reform.profilesync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableFeignClients
@SpringBootApplication
public class ProfilesyncApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProfilesyncApplication.class, args);
	}

}
