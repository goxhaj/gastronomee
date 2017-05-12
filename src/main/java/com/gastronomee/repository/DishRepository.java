package com.gastronomee.repository;

import com.gastronomee.domain.Dish;
import com.gastronomee.domain.Menu;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Dish entity.
 */
public interface DishRepository extends JpaRepository<Dish,Long> {

    @Query("select distinct dish from Dish dish left join fetch dish.ingredients")
    List<Dish> findAllWithEagerRelationships();

    @Query("select dish from Dish dish left join fetch dish.ingredients where dish.id =:id")
    Dish findOneWithEagerRelationships(@Param("id") Long id);

    List<Dish> findAllByMenuIn(List<Menu> menus);
	Page<Dish> findAllByMenuIn(List<Menu> menus, Pageable pageable);

}
