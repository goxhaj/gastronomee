package com.gastronomee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gastronomee.domain.DishCategory;

/**
 * Spring Data JPA repository for the DishCategory entity.
 */
public interface DishCategoryRepository extends JpaRepository<DishCategory,Long> {

	DishCategory findOneByUserLoginAndId(String currentUserLogin, Long id);

}
