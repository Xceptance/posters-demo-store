package com.xceptance.posters.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xceptance.posters.model.Cart;

public interface CartRepository extends JpaRepository<Cart, UUID>
{
}
