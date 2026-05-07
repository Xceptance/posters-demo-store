package com.xceptance.posters.repository;
import com.xceptance.posters.entity.Customer;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Customer entities.
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID>
{
    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);
}
