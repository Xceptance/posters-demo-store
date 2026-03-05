package com.xceptance.posters.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xceptance.posters.model.LocalizedPrice;

public interface LocalizedPriceRepository extends JpaRepository<LocalizedPrice, Integer>
{
}
