package com.gastronomee.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.gastronomee.domain.Dish;
import com.gastronomee.domain.Menu;

/**
 * Spring Data JPA repository for the Dish entity.
 */
public interface DishRepository extends JpaRepository<Dish,Long> {
	
    @Query("select dish from Dish dish where dish.user.login = ?#{principal.username}")
    Page<Dish> findByUserIsCurrentUser(Pageable pageable);

    @Query("select distinct dish from Dish dish left join fetch dish.ingredients")
    List<Dish> findAllWithEagerRelationships();

    @Query("select dish from Dish dish left join fetch dish.ingredients where dish.id =:id")
    Dish findOneWithEagerRelationships(@Param("id") Long id);

    List<Dish> findAllByMenuIn(List<Menu> menus);
	//Page<Dish> findAllByMenuIn(List<Menu> menus, Pageable pageable);

	Dish findOneByUserLoginAndId(String currentUserLogin, Long id);

}
