package com.xceptance.posters.repository;
import com.xceptance.posters.entity.AuditLogEntry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

/**
 * Spring Data repository for audit log entries.
 */
public interface AuditLogRepository extends JpaRepository<AuditLogEntry, Long> {

    @Query("SELECT e FROM AuditLogEntry e WHERE " +
           "(:username IS NULL OR LOWER(e.username) LIKE LOWER(CONCAT('%', :username, '%'))) AND " +
           "(:action IS NULL OR e.action = :action)")
    Page<AuditLogEntry> search(@Param("username") String username,
                               @Param("action") AuditLogEntry.Action action,
                               Pageable pageable);

    @Modifying
    @Query("DELETE FROM AuditLogEntry e WHERE e.timestamp < :cutoff")
    int deleteOlderThan(@Param("cutoff") LocalDateTime cutoff);
}
