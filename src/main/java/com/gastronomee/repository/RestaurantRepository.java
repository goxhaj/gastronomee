package com.gastronomee.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gastronomee.domain.Restaurant;

/**
 * Spring Data JPA repository for the Restaurant entity.
 */
public interface RestaurantRepository extends JpaRepository<Restaurant,Long> {

    @Query("select restaurant from Restaurant restaurant where restaurant.manager.login = ?#{principal.username}")
    List<Restaurant> findByManagerIsCurrentUser();

	Page<Restaurant> findByNameIgnoreCaseContaining(String name, Pageable page);

}
