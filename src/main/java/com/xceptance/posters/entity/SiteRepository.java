package com.xceptance.posters.entity;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Site entities.
 */
@Repository
public interface SiteRepository extends JpaRepository<Site, Integer>
{
    Optional<Site> findByName(String name);
}
