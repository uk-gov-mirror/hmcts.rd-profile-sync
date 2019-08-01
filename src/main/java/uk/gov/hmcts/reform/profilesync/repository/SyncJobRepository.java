package uk.gov.hmcts.reform.profilesync.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uk.gov.hmcts.reform.profilesync.domain.SyncJobAudit;

public interface SyncJobRepository extends JpaRepository<SyncJobAudit, Long> {


    List<SyncJobAudit> findByStatus(String status);

//    @Query("SELECT coalesce(max(s.audit_ts), 0) FROM dbsyncdata.sync_job s where s.status = ?1")
   // LocalDateTime getStatus(String status) ;

    SyncJobAudit findFirstByStatusOrderByAuditTsDesc(String status);

}
