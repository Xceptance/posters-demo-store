package com.xceptance.posters.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xceptance.posters.model.Product;
import com.xceptance.posters.model.SubCategory;
import com.xceptance.posters.model.TopCategory;

public interface ProductRepository extends JpaRepository<Product, Integer>
{
    List<Product> findByShowInCarouselTrue();

    List<Product> findBySubCategory(SubCategory subCategory);

    List<Product> findByTopCategory(TopCategory topCategory);
}
