package com.xceptance.posters.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for the new Category entity (2-level hierarchy).
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

    /**
     * Find all top-level categories (those without a parent).
     */
    List<Category> findByParentIsNull();

    /**
     * Find all subcategories of a given parent category.
     */
    List<Category> findByParent(Category parent);

    /**
     * Find subcategories by parent id.
     */
    List<Category> findByParentId(Integer parentId);
}
