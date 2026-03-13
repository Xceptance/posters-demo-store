package com.xceptance.posters.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SystemStatusRepository extends JpaRepository<SystemStatus, String> {
    Optional<SystemStatus> findByKey(String key);
}
