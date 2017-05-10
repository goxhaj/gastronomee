package com.gastronomee.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.gastronomee.domain.Dish;

import com.gastronomee.repository.DishRepository;
import com.gastronomee.repository.search.DishSearchRepository;
import com.gastronomee.web.rest.util.HeaderUtil;
import com.gastronomee.web.rest.util.PaginationUtil;
import io.swagger.annotations.ApiParam;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

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

    public DishResource(DishRepository dishRepository, DishSearchRepository dishSearchRepository) {
        this.dishRepository = dishRepository;
        this.dishSearchRepository = dishSearchRepository;
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
    public ResponseEntity<Dish> createDish(@Valid @RequestBody Dish dish) throws URISyntaxException {
        log.debug("REST request to save Dish : {}", dish);
        if (dish.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new dish cannot already have an ID")).body(null);
        }
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
    public ResponseEntity<Dish> updateDish(@Valid @RequestBody Dish dish) throws URISyntaxException {
        log.debug("REST request to update Dish : {}", dish);
        if (dish.getId() == null) {
            return createDish(dish);
        }
        Dish result = dishRepository.save(dish);
        dishSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, dish.getId().toString()))
            .body(result);
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
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        log.debug("REST request to delete Dish : {}", id);
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


}
