package com.gastronomee.repository;

import com.gastronomee.domain.DishOrder;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the DishOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DishOrderRepository extends JpaRepository<DishOrder,Long> {

}
