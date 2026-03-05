package com.xceptance.posters.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xceptance.posters.model.Product;
import com.xceptance.posters.model.ProductPosterSize;
import com.xceptance.posters.model.PosterSize;

public interface ProductPosterSizeRepository extends JpaRepository<ProductPosterSize, Integer>
{
    ProductPosterSize findByProductAndSize(Product product, PosterSize size);
    ProductPosterSize findByProductAndSizeAndFinish(Product product, PosterSize size, String finish);
    List<ProductPosterSize> findByProductAndFinish(Product product, String finish);
}
