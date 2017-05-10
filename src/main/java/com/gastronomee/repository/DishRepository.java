package com.gastronomee.repository;

import com.gastronomee.domain.Dish;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Spring Data JPA repository for the Dish entity.
 */
@SuppressWarnings("unused")
public interface DishRepository extends JpaRepository<Dish,Long> {

    @Query("select distinct dish from Dish dish left join fetch dish.ingredients")
    List<Dish> findAllWithEagerRelationships();

    @Query("select dish from Dish dish left join fetch dish.ingredients where dish.id =:id")
    Dish findOneWithEagerRelationships(@Param("id") Long id);

}
