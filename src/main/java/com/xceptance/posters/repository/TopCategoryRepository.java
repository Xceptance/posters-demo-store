package com.xceptance.posters.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xceptance.posters.model.TopCategory;

public interface TopCategoryRepository extends JpaRepository<TopCategory, Integer>
{
}
