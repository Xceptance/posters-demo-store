package com.xceptance.posters.config;
import com.xceptance.posters.util.AdminDataLoader;

import com.xceptance.posters.entity.SystemStatus;
import com.xceptance.posters.repository.SystemStatusRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * DB-backed readiness gate for the backoffice.
 *
 * A "backoffice.ready" entry in the system_status table is the source of truth.
 * An in-memory volatile flag caches the result so DB lookups only happen once
 * per JVM lifecycle (not on every request).
 *
 * On fresh start: seeding → markReady() writes to DB + sets flag.
 * On restart with existing data: AdminDataLoader detects existing data and still calls
 * markReady(), so the flag is set again for the new JVM.
 */
@Component
public class BackofficeReadinessService {

    private static final Logger log = LoggerFactory.getLogger(BackofficeReadinessService.class);
    static final String READY_KEY = "backoffice.ready";
    static final String READY_VALUE = "true";

    private volatile boolean ready = false;

    private final SystemStatusRepository systemStatusRepository;

    public BackofficeReadinessService(SystemStatusRepository systemStatusRepository) {
        this.systemStatusRepository = systemStatusRepository;
    }

    /**
     * Returns true if the backoffice is ready to serve requests.
     * Checks the in-memory cache first; falls back to DB once if not yet ready.
     */
    public boolean isReady() {
        if (ready) {
            return true;
        }
        // Check DB — this path only occurs during the brief startup window
        boolean dbReady = systemStatusRepository.findByKey(READY_KEY)
                .map(s -> READY_VALUE.equals(s.getValue()))
                .orElse(false);
        if (dbReady) {
            ready = true;
        }
        return dbReady;
    }

    /**
     * Called by AdminDataLoader when seeding is complete.
     * Writes the ready marker to the DB and sets the in-memory flag.
     */
    public void markReady() {
        systemStatusRepository.findByKey(READY_KEY).ifPresentOrElse(
                existing -> {
                    existing.setValue(READY_VALUE);
                    systemStatusRepository.save(existing);
                },
                () -> systemStatusRepository.save(new SystemStatus(READY_KEY, READY_VALUE))
        );
        ready = true;
        log.info("Backoffice readiness marker written to DB — system is ready");
    }
}
