package com.xceptance.posters.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for the new CatalogOrder entity.
 */
@Repository
public interface CatalogOrderRepository extends JpaRepository<CatalogOrder, java.util.UUID> {

    /**
     * Find an order by its order number.
     */
    Optional<CatalogOrder> findByOrderNumber(String orderNumber);
}
