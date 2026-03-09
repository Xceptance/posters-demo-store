package com.xceptance.posters.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for the new Product entity (catalog products with variants).
 */
@Repository
public interface CatalogProductRepository extends JpaRepository<Product, Integer> {

    /**
     * Find a product by its SKU.
     */
    Optional<Product> findBySku(String sku);

    /**
     * Find all products in a given category.
     */
    @Query("SELECT p FROM CatalogProduct p JOIN p.categories c WHERE c = :category")
    List<Product> findByCategory(@Param("category") Category category);

    /**
     * Find all products in a given category by category id.
     */
    @Query("SELECT p FROM CatalogProduct p JOIN p.categories c WHERE c.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") Integer categoryId);

    /**
     * Find all products flagged for the homepage carousel.
     */
    List<Product> findByShowInCarouselTrue();
}
