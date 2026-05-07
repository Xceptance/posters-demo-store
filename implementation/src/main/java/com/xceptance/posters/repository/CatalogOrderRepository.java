package com.xceptance.posters.repository;
import com.xceptance.posters.entity.CatalogOrder;

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

    java.util.List<CatalogOrder> findByCustomer_EmailOrderByOrderDateDesc(String email);

    long countByCustomer_Id(java.util.UUID customerId);
}
