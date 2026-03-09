package com.xceptance.posters.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for the new Variant entity.
 */
@Repository
public interface VariantRepository extends JpaRepository<Variant, Integer> {

    /**
     * Find all variants for a given product.
     */
    List<Variant> findByProduct(Product product);

    /**
     * Find all variants for a given product id.
     */
    List<Variant> findByProductId(Integer productId);
}
