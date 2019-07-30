package uk.gov.hmcts.reform.profilesync.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.reform.profilesync.domain.SyncJobAudit;

public interface SyncJobRepository extends JpaRepository<SyncJobAudit, Long> {


    Optional<SyncJobAudit> findByStatus(String now);

}
