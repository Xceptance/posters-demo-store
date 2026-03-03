package com.xceptance.posters.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xceptance.posters.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, UUID>
{
    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);
}
