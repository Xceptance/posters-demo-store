package com.xceptance.posters.entity;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for CatalogCustomer entities.
 */
@Repository
public interface CatalogCustomerRepository extends JpaRepository<CatalogCustomer, UUID>
{
    Optional<CatalogCustomer> findByEmail(String email);

    boolean existsByEmail(String email);
}
