package com.xceptance.posters.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xceptance.posters.model.Cart;
import com.xceptance.posters.model.CartProduct;
import com.xceptance.posters.model.PosterSize;
import com.xceptance.posters.model.Product;

public interface CartProductRepository extends JpaRepository<CartProduct, Integer>
{
    List<CartProduct> findByCartOrderByIdDesc(Cart cart);

    CartProduct findByCartAndProductAndFinishAndSize(Cart cart, Product product, String finish, PosterSize size);
}
