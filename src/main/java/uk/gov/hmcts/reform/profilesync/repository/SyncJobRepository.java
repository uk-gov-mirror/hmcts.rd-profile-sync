package uk.gov.hmcts.reform.profilesync.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.profilesync.domain.SyncJobAudit;

public interface SyncJobRepository extends JpaRepository<SyncJobAudit, Long> {

    List<SyncJobAudit> findByStatus(String status);

    SyncJobAudit findFirstByStatusOrderByAuditTsDesc(String status);

}
