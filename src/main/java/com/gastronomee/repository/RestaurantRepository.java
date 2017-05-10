package com.gastronomee.repository;

import com.gastronomee.domain.Restaurant;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Restaurant entity.
 */
@SuppressWarnings("unused")
public interface RestaurantRepository extends JpaRepository<Restaurant,Long> {

    @Query("select restaurant from Restaurant restaurant where restaurant.manager.login = ?#{principal.username}")
    List<Restaurant> findByManagerIsCurrentUser();

}
