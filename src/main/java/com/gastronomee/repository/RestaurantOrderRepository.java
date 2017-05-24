package com.gastronomee.repository;

import com.gastronomee.domain.RestaurantOrder;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;
import java.util.List;

/**
 * Spring Data JPA repository for the RestaurantOrder entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder,Long> {

    @Query("select restaurant_order from RestaurantOrder restaurant_order where restaurant_order.user.login = ?#{principal.username}")
    List<RestaurantOrder> findByUserIsCurrentUser();

}
