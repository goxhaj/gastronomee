package com.gastronomee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gastronomee.domain.Ingredient;

/**
 * Spring Data JPA repository for the Ingredient entity.
 */

public interface IngredientRepository extends JpaRepository<Ingredient,Long> {

	Ingredient findOneByUserLoginAndId(String currentUserLogin, Long id);


}
