package com.gastronomee.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.gastronomee.domain.Dish;
import com.gastronomee.domain.Location;
import com.gastronomee.domain.Menu;
import com.gastronomee.domain.Restaurant;
import com.gastronomee.repository.DishRepository;
import com.gastronomee.repository.LocationRepository;
import com.gastronomee.repository.MenuRepository;
import com.gastronomee.repository.RestaurantRepository;
import com.gastronomee.repository.UserRepository;
import com.gastronomee.repository.search.LocationSearchRepository;
import com.gastronomee.repository.search.RestaurantSearchRepository;
import com.gastronomee.security.AuthoritiesConstants;
import com.gastronomee.security.SecurityUtils;
import com.gastronomee.web.rest.util.HeaderUtil;
import com.gastronomee.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing Restaurant.
 */
@RestController
@RequestMapping("/api")
public class RestaurantResource {

    private final Logger log = LoggerFactory.getLogger(RestaurantResource.class);

    private static final String ENTITY_NAME = "restaurant";
        
    private final RestaurantRepository restaurantRepository;

    private final RestaurantSearchRepository restaurantSearchRepository;
    
    private final MenuRepository menuRepository;
    
    private final LocationRepository locationRepository;

    private final LocationSearchRepository locationSearchRepository;
    
    private final UserRepository userRepository;
    
    private final DishRepository dishRepository;

    public RestaurantResource(RestaurantRepository restaurantRepository, 
    		RestaurantSearchRepository restaurantSearchRepository,
    		LocationRepository locationRepository, 
    		LocationSearchRepository locationSearchRepository,
    		MenuRepository menuRepository,
    		UserRepository userRepository,
    	    DishRepository dishRepository) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantSearchRepository = restaurantSearchRepository;
        this.locationRepository = locationRepository;
        this.locationSearchRepository = locationSearchRepository;
        this.menuRepository = menuRepository;
        this.userRepository = userRepository;
        this.dishRepository = dishRepository;
    }

    /**
     * POST  /restaurants : Create a new restaurant.
     *
     * @param restaurant the restaurant to create
     * @return the ResponseEntity with status 201 (Created) and with body the new restaurant, or with status 400 (Bad Request) if the restaurant has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/restaurants")
    @Timed
    @Secured({
    	AuthoritiesConstants.ADMIN,
    	AuthoritiesConstants.MANAGER,
    })
    public ResponseEntity<Restaurant> createRestaurant(@Valid @RequestBody Restaurant restaurant) throws URISyntaxException {
        log.debug("REST request to save Restaurant : {}", restaurant);
        if (restaurant.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new restaurant cannot already have an ID")).body(null);
        }
        
        //when a restaurant is created then a new location is created 
        if(restaurant.getLocation()!=null){
        	Location location = locationRepository.save(restaurant.getLocation());
        	locationSearchRepository.save(location);
        }
        
        
        //the manager is the current user that is creating this restaurant
        restaurant.setManager(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        
        Restaurant result = restaurantRepository.save(restaurant);
        restaurantSearchRepository.save(result);
        
        return ResponseEntity.created(new URI("/api/restaurants/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /restaurants : Updates an existing restaurant.
     *
     * @param restaurant the restaurant to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated restaurant,
     * or with status 400 (Bad Request) if the restaurant is not valid,
     * or with status 500 (Internal Server Error) if the restaurant couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/restaurants")
    @Timed
    @Secured({
    	AuthoritiesConstants.ADMIN,
    	AuthoritiesConstants.MANAGER,
    })
    public ResponseEntity<Restaurant> updateRestaurant(@Valid @RequestBody Restaurant restaurant) throws URISyntaxException {
        log.debug("REST request to update Restaurant : {}", restaurant);
        
        if (restaurant!=null &&  !hasPermission(restaurant.getId())) {
        	return ResponseEntity.status(401)//access denied
        			.headers(HeaderUtil.createFailureAlert(ENTITY_NAME, restaurant.getId().toString(), "Permission denied!")).build();
        }
        
        if (restaurant.getId() == null) {
            return createRestaurant(restaurant);
        }
        
        if(restaurant.getLocation()!=null){
        	Location location = locationRepository.save(restaurant.getLocation());
        	locationSearchRepository.save(location);
        }
        
        Restaurant result = restaurantRepository.save(restaurant);
        restaurantSearchRepository.save(result);
        
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, restaurant.getId().toString()))
            .body(result);
    }
    
    /**
     * GET  /restaurants/my : get all my restaurants.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of restaurants in body
     */
    @GetMapping("/restaurants/my")
    @Timed
    public ResponseEntity<List<Restaurant>> getMyRestaurants(@ApiParam Pageable pageable) {
        log.debug("REST request to get my Restaurants");
        Page<Restaurant> page = restaurantRepository.findAllByManagerLogin(SecurityUtils.getCurrentUserLogin(), pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/restaurants/my");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /restaurants : get all the restaurants.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of restaurants in body
     */
    @GetMapping("/restaurants")
    @Timed
    public ResponseEntity<List<Restaurant>> getAllRestaurants(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Restaurants");
       
        Page<Restaurant> page = restaurantRepository.findAll(pageable);//admin sees all restaurants
        /*if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
        	page = restaurantRepository.findAll(pageable);//admin sees all restaurants
        } else if(SecurityUtils.) {
        	page = restaurantRepository.findAllByManagerLogin(SecurityUtils.getCurrentUserLogin(), pageable);
        }*/
        
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/restaurants");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    /**
     * GET  /restaurant/:id/menus : get the menus of the restaurant with "id" .
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of menus in body
     */
    @GetMapping("/restaurants/{id}/menus")
    @Timed
    public ResponseEntity<List<Menu>> getAllMenusByRestaurant(@PathVariable Long id, @ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Menus");
        Page<Menu> page = menuRepository.findAllByRestaurantId(id, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/restaurants/{id}/menus");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    /**
     * GET  /restaurants/{id}/dishes : get all the restaurant dishes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of restaurants in body
     */
    @GetMapping("/restaurants/{id}/dishes")
    @Timed
    @Secured({
    	AuthoritiesConstants.ADMIN,
    	AuthoritiesConstants.MANAGER,
    })
    public ResponseEntity<List<Dish>> getAllDishesByRestaurant(@PathVariable Long id, @ApiParam Pageable pageable) {
        log.debug("REST request to get restaurant Dishes");
        
        Restaurant restaurant = restaurantRepository.findOne(id);
        List<Menu> menus = menuRepository.findAllByRestaurant(restaurant);

        List<Dish> page = dishRepository.findAllByMenuIn(menus);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    /**
     * GET  /restaurants/:id : get the "id" restaurant.
     *
     * @param id the id of the restaurant to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the restaurant, or with status 404 (Not Found)
     */
    @GetMapping("/restaurants/{id}")
    @Timed
    public ResponseEntity<Restaurant> getRestaurant(@PathVariable Long id) {
        log.debug("REST request to get Restaurant : {}", id);
        Restaurant restaurant = restaurantRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(restaurant));
    }

    /**
     * DELETE  /restaurants/:id : delete the "id" restaurant.
     *
     * @param id the id of the restaurant to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/restaurants/{id}")
    @Timed
    @Secured({
    	AuthoritiesConstants.ADMIN,
    	AuthoritiesConstants.MANAGER,
    })
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long id) {
        log.debug("REST request to delete Restaurant : {}", id);
        
        if (!hasPermission(id)) {
        	return ResponseEntity.status(401)//access denied
        			.headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        }    

        restaurantRepository.delete(id);
        restaurantSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();

        
    }
    
    /**
     * SEARCH  /_search/restaurants?query=:query : search for the restaurant corresponding
     * to the query.
     *
     * @param query the query of the restaurant search 
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/restaurants")
    @Timed
    public ResponseEntity<List<Restaurant>> searchRestaurants(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Restaurants for query {}", query);
        Page<Restaurant> page = restaurantSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/restaurants");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    private boolean hasPermission(Long id){
    	if(restaurantRepository.findOneByManagerLoginAndId(SecurityUtils.getCurrentUserLogin(), id) !=null || SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)){
    		return true;
    	} else {
    		log.warn("Permission denied of User with ID: {}", id);
    		return false;
    	}
    }

}
