package uk.gov.hmcts.reform.profilesync.util;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.profilesync.domain.Source;
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

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-DD HH:mm:ss");


    @Scheduled(cron = "0 0 * * * *")
    public void updateIdamDataWithUserProfile() {

        log.info("The time is now {}", dateFormat.format(new Date()));

        String searchQuery = "roles:\"pui-case-manager\" OR roles:\"pui-user-manager\" OR roles:\"pui-organisation-manager\" OR roles:\"pui-finance-manager\" AND lastModified:>now-1h";

        List<SyncJobAudit>  syncJobAudits = syncJobRepository.findAll();

        log.info("List::SIZE" + syncJobAudits.size());

        if (null != syncJobRepository.findFirstByStatusOrderByAuditTsDesc("fail")) {

            SyncJobAudit auditjob = syncJobRepository.findFirstByStatusOrderByAuditTsDesc("success");
            searchQuery =  searchQuery.replace("1",getLastBatchFailureTimeInHours(auditjob.getAuditTs()));
            log.info("searchQuery::",searchQuery);
        }
        try {

            profileSyncService.updateUserProfileFeed(searchQuery);
            SyncJobAudit syncJobAudit = new SyncJobAudit(201, "success", Source.SYNC);
            syncJobRepository.save(syncJobAudit);
        } catch (Exception e) {
            SyncJobAudit syncJobAudit = new SyncJobAudit(500, "fail", Source.SYNC);
            syncJobRepository.save(syncJobAudit);
            log.error("Sync Batch Job Failed::",e);
        }
    }

    /**
     *
     * @param lastSuccessBatch
     * @return
     */
    private String getLastBatchFailureTimeInHours(LocalDateTime lastSuccessBatch) {

        long hoursDiff = 1;
        Duration duration = Duration.between(LocalDateTime.now(), lastSuccessBatch);
        long minutesDiff = Math.abs(duration.toMinutes());
        if (minutesDiff > 60) {
            hoursDiff = minutesDiff/60;
            if (minutesDiff%60 > 0) {
                hoursDiff = hoursDiff + 1;
            }
            log.info("Diff of Hours::" + hoursDiff);
        }
        log.info("Since Last Batch failure in sync job in hours:: " + hoursDiff);
        return Long.valueOf(hoursDiff).toString();
    }

}





