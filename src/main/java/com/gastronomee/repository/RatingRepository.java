package com.gastronomee.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gastronomee.domain.Rating;
import com.gastronomee.domain.User;

/**
 * Spring Data JPA repository for the Rating entity.
 */
public interface RatingRepository extends JpaRepository<Rating,Long> {

    @Query("select rating from Rating rating where rating.user.login = ?#{principal.username}")
    List<Rating> findByUserIsCurrentUser();

	Page<Rating> findAllByRestaurantId(Long id, Pageable pageable);

	@Query("select rating from Rating rating where rating.restaurant.id= ?1 and rating.user = ?2")
	List<Rating> findByRestaurantIdAndUserIsCurrentUser(Long restaurantId, User user);

}
