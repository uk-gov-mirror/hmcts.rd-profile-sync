package uk.gov.hmcts.reform.profilesync.util;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.profilesync.domain.SyncJobAudit;
import uk.gov.hmcts.reform.profilesync.repository.SyncJobRepository;
import uk.gov.hmcts.reform.profilesync.service.ProfileSyncService;

@ConfigurationProperties(prefix = "idam.sync")
@Configuration
@Component
@Slf4j
public class UserProfileSyncJobScheduler {

    @Autowired
    protected ProfileSyncService profileSyncService;

    @Autowired
    protected SyncJobRepository syncJobRepository;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    int count = 0;
    @Scheduled(cron = "*/500 * * * * *")
    public void updateIdamDataWithUserProfile() {

        log.info("The time is now {}", dateFormat.format(new Date()));
        String searchQuery = "lastModified:>now-1h";

        log.info("searchQuery::",searchQuery);

        if (null != syncJobRepository.findFirstByStatusOrderByAuditTsDesc("fail")) {

            SyncJobAudit auditjob = syncJobRepository.findFirstByStatusOrderByAuditTsDesc("success");
            log.info("Now::" +  LocalDateTime.now());
            log.info("Last Success::" + auditjob.getAuditTs());

            log.info("DIFF::" + LocalDateTime.now().minusHours(auditjob.getAuditTs().getHour()));

            // profileSyncService.updateUserProfileFeed("lastModified:>now-1h");

        } else {

            log.info("Sync job running every hour::");
            profileSyncService.updateUserProfileFeed("roles:\"pui-case-manager\" OR \"pui-user-manager\" OR \"pui-organisation-manager\" OR \"pui-finance-manager\"", count);
        }

    }

}





