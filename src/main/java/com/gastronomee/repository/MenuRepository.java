package com.gastronomee.repository;

import com.gastronomee.domain.Menu;
import com.gastronomee.domain.Restaurant;
import com.gastronomee.domain.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Menu entity.
 */
@SuppressWarnings("unused")
public interface MenuRepository extends JpaRepository<Menu,Long> {

	Page<Menu> findAllByRestaurantIdAndActiveTrue(Long id, Pageable pageable);
	
	List<Menu> findAllByRestaurantIn(List<Restaurant> restaurants);
	Page<Menu> findAllByRestaurantIn(List<Restaurant> restaurants, Pageable pageable);
	
	List<Menu> findAllByRestaurantAndActiveTrue(Restaurant restaurant);
	
	

}
