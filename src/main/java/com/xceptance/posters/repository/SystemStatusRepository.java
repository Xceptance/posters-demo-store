package com.xceptance.posters.repository;
import com.xceptance.posters.entity.SystemStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemStatusRepository extends JpaRepository<SystemStatus, String> {
    Optional<SystemStatus> findByKey(String key);
}
