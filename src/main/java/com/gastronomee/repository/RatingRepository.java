package com.gastronomee.repository;

import com.gastronomee.domain.Rating;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Rating entity.
 */
public interface RatingRepository extends JpaRepository<Rating,Long> {

    @Query("select rating from Rating rating where rating.user.login = ?#{principal.username}")
    List<Rating> findByUserIsCurrentUser();

	Page<Rating> findAllByRestaurantId(Long id, Pageable pageable);

}
