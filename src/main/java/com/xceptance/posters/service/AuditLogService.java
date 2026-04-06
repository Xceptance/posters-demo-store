package com.xceptance.posters.service;

import com.xceptance.posters.entity.AuditLogEntry;
import com.xceptance.posters.entity.AuditLogEntry.Action;
import com.xceptance.posters.entity.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for recording and querying audit log entries.
 * Includes scheduled cleanup of entries older than 180 days.
 */
@Service
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);
    private static final int RETENTION_DAYS = 180;

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(Long userId, String username, Action action,
                    String targetType, Long targetId, String details) {
        AuditLogEntry entry = new AuditLogEntry();
        entry.setUserId(userId);
        entry.setUsername(username);
        entry.setAction(action);
        entry.setTargetType(targetType);
        entry.setTargetId(targetId);
        entry.setDetails(details);
        auditLogRepository.save(entry);
    }

    public Page<AuditLogEntry> search(String username, Action action, Pageable pageable) {
        return auditLogRepository.search(
                username != null && !username.isBlank() ? username : null,
                action,
                pageable);
    }

    @Transactional
    @Scheduled(cron = "0 0 2 * * *") // Run daily at 2 AM
    public void cleanup() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(RETENTION_DAYS);
        int deleted = auditLogRepository.deleteOlderThan(cutoff);
        if (deleted > 0) {
            log.info("Audit log cleanup: removed {} entries older than {} days", deleted, RETENTION_DAYS);
        }
    }
}
