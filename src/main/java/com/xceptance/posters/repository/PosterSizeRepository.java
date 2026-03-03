package com.xceptance.posters.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.xceptance.posters.model.PosterSize;

public interface PosterSizeRepository extends JpaRepository<PosterSize, Integer>
{
    PosterSize findByWidthAndHeight(int width, int height);
}
