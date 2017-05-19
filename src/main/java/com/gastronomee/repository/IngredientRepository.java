package com.gastronomee.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.gastronomee.domain.Ingredient;

/**
 * Spring Data JPA repository for the Ingredient entity.
 */

public interface IngredientRepository extends JpaRepository<Ingredient,Long> {

	Ingredient findOneByUserLoginAndId(String currentUserLogin, Long id);

	List<Ingredient> findByNameIgnoreCaseContainingAndActiveTrue(String name);

	Page<Ingredient> findAllByActiveTrue(Pageable pageable);


}
