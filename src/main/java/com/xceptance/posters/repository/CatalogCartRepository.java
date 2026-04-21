package com.xceptance.posters.repository;
import com.xceptance.posters.entity.CatalogCart;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for the new CatalogCart entity.
 */
@Repository
public interface CatalogCartRepository extends JpaRepository<CatalogCart, java.util.UUID> {
}
