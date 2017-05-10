package com.gastronomee.repository;

import com.gastronomee.domain.DishCategory;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the DishCategory entity.
 */
@SuppressWarnings("unused")
public interface DishCategoryRepository extends JpaRepository<DishCategory,Long> {

}
