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

    int count = 0;
    @Scheduled(cron = "*/500 * * * * *")
    public void updateIdamDataWithUserProfile() {

        log.info("The time is now {}", dateFormat.format(new Date()));
       // String searchQuery = "/api/v1/users?page=0&query=lastModified:%5B$NOWMINUSONEHOUR$ TO $NOW$%5D AND (roles:pui-case-manager OR roles:pui-user-manager)";
        String searchQuery = "roles:\"pui-case-manager\" OR roles:\"pui-user-manager\" OR roles:\"pui-organisation-manager\" OR roles:\"pui-finance-manager\"  AND lastModified:>now-5h";

        log.info("searchQuery::",searchQuery);

        List<SyncJobAudit>  syncJobAudits = syncJobRepository.findAll();

        log.info("List::SIZE" + syncJobAudits.size());

        if (null != syncJobRepository.findFirstByStatusOrderByAuditTsDesc("fail")) {

            SyncJobAudit auditjob = syncJobRepository.findFirstByStatusOrderByAuditTsDesc("success");

            log.info("Last Success::" + auditjob.getAuditTs() + ":: Now :" + LocalDateTime.now());
            searchQuery =  searchQuery.replace("1",getLastBatchFailureTimeInHours(auditjob.getAuditTs()));
            profileSyncService.updateUserProfileFeed(searchQuery,count);

        } else {

           /*searchQuery = searchQuery.replace("$NOWMINUSONEHOUR$",LocalDateTime.now().minusHours(1).toString());
           log.info("NowMINUS::" +  searchQuery);
           searchQuery = searchQuery.replace("$NOW$",LocalDateTime.now().toString());
           log.info("NOW::" + searchQuery);*/
           String searchQuery1 = "roles:\"pui-case-manager\" OR roles:\"pui-user-manager\" OR roles:\"pui-organisation-manager\" OR roles:\"pui-finance-manager\"$PAGE$";
           log.info("Sync job running every hour::");
           profileSyncService.updateUserProfileFeed(searchQuery1, count);
        }

    }

    /**
     *
     * @param lastSuccessBatch
     * @return
     */
    private String getLastBatchFailureTimeInHours(LocalDateTime lastSuccessBatch) {

        long hoursDiff = 0;
        Duration duration = Duration.between(LocalDateTime.now(), lastSuccessBatch);
        //long diff = Math.abs(duration.toHours());
        long minutesDiff = Math.abs(duration.toMinutes());

        if (minutesDiff > 60) {
            hoursDiff = minutesDiff/60;
            if (minutesDiff%60 > 0) {
                hoursDiff = hoursDiff + 1;
            }
            log.info("Diff of Hour::" + hoursDiff);
        } else {

            hoursDiff = 1;
        }
        log.info("Since Last Batch failure in sync job in hours:: " + hoursDiff);
        return Long.valueOf(hoursDiff).toString();
    }

}





