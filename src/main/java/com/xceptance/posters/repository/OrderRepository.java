package com.xceptance.posters.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xceptance.posters.model.Customer;
import com.xceptance.posters.model.Order;

public interface OrderRepository extends JpaRepository<Order, UUID>
{
    List<Order> findByCustomerOrderByOrderDateDesc(Customer customer);
}
