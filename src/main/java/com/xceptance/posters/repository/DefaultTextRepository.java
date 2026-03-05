package com.xceptance.posters.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xceptance.posters.model.DefaultText;

public interface DefaultTextRepository extends JpaRepository<DefaultText, Integer>
{
}
