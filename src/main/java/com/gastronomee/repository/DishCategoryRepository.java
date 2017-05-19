package com.gastronomee.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gastronomee.domain.DishCategory;

/**
 * Spring Data JPA repository for the DishCategory entity.
 */
public interface DishCategoryRepository extends JpaRepository<DishCategory,Long> {

	DishCategory findOneByUserLoginAndId(String currentUserLogin, Long id);

	Page<DishCategory> findAllByActiveTrue(Pageable pageable);

	@Query("select dishCategory from DishCategory dishCategory where dishCategory.user.login = ?#{principal.username}")
	Page<DishCategory> findByUserIsCurrentUser(Pageable pageable);

}
