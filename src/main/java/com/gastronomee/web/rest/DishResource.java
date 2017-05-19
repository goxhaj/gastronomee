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
import com.gastronomee.repository.DishRepository;
import com.gastronomee.repository.UserRepository;
import com.gastronomee.repository.search.DishSearchRepository;
import com.gastronomee.security.AuthoritiesConstants;
import com.gastronomee.security.SecurityUtils;
import com.gastronomee.web.rest.util.HeaderUtil;
import com.gastronomee.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing Dish.
 */
@RestController
@RequestMapping("/api")
public class DishResource {

    private final Logger log = LoggerFactory.getLogger(DishResource.class);

    private static final String ENTITY_NAME = "dish";
        
    private final DishRepository dishRepository;

    private final DishSearchRepository dishSearchRepository;
    
    private final UserRepository userRepository;

    public DishResource(DishRepository dishRepository, DishSearchRepository dishSearchRepository,  UserRepository userRepository) {
        this.dishRepository = dishRepository;
        this.dishSearchRepository = dishSearchRepository;
        this.userRepository = userRepository;
    }

    /**
     * POST  /dishes : Create a new dish.
     *
     * @param dish the dish to create
     * @return the ResponseEntity with status 201 (Created) and with body the new dish, or with status 400 (Bad Request) if the dish has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/dishes")
    @Timed
    @Secured({
    	AuthoritiesConstants.ADMIN,
    	AuthoritiesConstants.MANAGER,
    })
    public ResponseEntity<Dish> createDish(@Valid @RequestBody Dish dish) throws URISyntaxException {
        log.debug("REST request to save Dish : {}", dish);
        if (dish.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new dish cannot already have an ID")).body(null);
        }
        if (dish.getMenu() == null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "menurequired", "A menu is required to create a dish")).body(null);
        }
        
        //the current user that is creating this dish
        dish.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        
        Dish result = dishRepository.save(dish);
        
        dishSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/dishes/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /dishes : Updates an existing dish.
     *
     * @param dish the dish to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated dish,
     * or with status 400 (Bad Request) if the dish is not valid,
     * or with status 500 (Internal Server Error) if the dish couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/dishes")
    @Timed
    @Secured({
    	AuthoritiesConstants.ADMIN,
    	AuthoritiesConstants.MANAGER,
    })
    public ResponseEntity<Dish> updateDish(@Valid @RequestBody Dish dish) throws URISyntaxException {
        log.debug("REST request to update Dish : {}", dish);
        if (dish.getId() == null) {
            return createDish(dish);
        }
        if (dish.getMenu() == null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "menurequired", "A menu is required to update a dish")).body(null);
        }
        
        if (!hasPermission(dish.getId())) {
        	return ResponseEntity.status(401)//access denied
        			.headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, dish.getId().toString())).build();
        } 
        
        Dish result = dishRepository.save(dish);
        dishSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, dish.getId().toString()))
            .body(result);
    }
    
    
    /**
     * GET  /dishes/my : get all my dishes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of restaurants in body
     */
    @GetMapping("/dishes/my")
    @Timed
    @Secured({
    	AuthoritiesConstants.ADMIN,
    	AuthoritiesConstants.MANAGER,
    })
    public ResponseEntity<List<Dish>> getMyDishes(@ApiParam Pageable pageable) {
        log.debug("REST request to get my Dishes");
        
        Page<Dish> page = dishRepository.findByUserIsCurrentUser(pageable);
        //List<Menu> menus = menuRepository.findAllByRestaurantIn(restaurants);

        //Page<Dish> page = dishRepository.findAllByMenuIn(menus, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/dishes/my");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    
    /**
     * GET  /dishes/active : get all the dishes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of dishes in body
     */
    @GetMapping("/dishes/active")
    @Timed
    public ResponseEntity<List<Dish>> getActiveDishes(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of active Dishes");
        Page<Dish> page = dishRepository.findAllByActiveTrue(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/dishes/active");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
   

    /**
     * GET  /dishes : get all the dishes.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of dishes in body
     */
    @GetMapping("/dishes")
    @Timed
    public ResponseEntity<List<Dish>> getAllDishes(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Dishes");
        Page<Dish> page = dishRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/dishes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /dishes/:id : get the "id" dish.
     *
     * @param id the id of the dish to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the dish, or with status 404 (Not Found)
     */
    @GetMapping("/dishes/{id}")
    @Timed
    public ResponseEntity<Dish> getDish(@PathVariable Long id) {
        log.debug("REST request to get Dish : {}", id);
        Dish dish = dishRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(dish));
    }

    /**
     * DELETE  /dishes/:id : delete the "id" dish.
     *
     * @param id the id of the dish to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/dishes/{id}")
    @Timed
    @Secured({
    	AuthoritiesConstants.ADMIN,
    	AuthoritiesConstants.MANAGER,
    })
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        log.debug("REST request to delete Dish : {}", id);
        
        if (!hasPermission(id)) {
        	return ResponseEntity.status(401)//access denied
        			.headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } 
        
        dishRepository.delete(id);
        dishSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/dishes?query=:query : search for the dish corresponding
     * to the query.
     *
     * @param query the query of the dish search 
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/dishes")
    @Timed
    public ResponseEntity<List<Dish>> searchDishes(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Dishes for query {}", query);
        Page<Dish> page = dishSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/dishes");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    private boolean hasPermission(Long id){
    	if(dishRepository.findOneByUserLoginAndId(SecurityUtils.getCurrentUserLogin(), id) !=null || SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)){
    		return true;
    	} else {
    		log.warn("Permission denied of User with ID: {}", id);
    		return false;
    	}
    }


}
