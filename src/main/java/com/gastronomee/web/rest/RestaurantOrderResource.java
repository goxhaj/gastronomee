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
import com.gastronomee.domain.RestaurantOrder;
import com.gastronomee.repository.RestaurantOrderRepository;
import com.gastronomee.repository.search.RestaurantOrderSearchRepository;
import com.gastronomee.web.rest.util.HeaderUtil;
import com.gastronomee.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing RestaurantOrder.
 */
@RestController
@RequestMapping("/api")
public class RestaurantOrderResource {

    private final Logger log = LoggerFactory.getLogger(RestaurantOrderResource.class);

    private static final String ENTITY_NAME = "restaurantOrder";
        
    private final RestaurantOrderRepository restaurantOrderRepository;

    private final RestaurantOrderSearchRepository restaurantOrderSearchRepository;

    public RestaurantOrderResource(RestaurantOrderRepository restaurantOrderRepository, RestaurantOrderSearchRepository restaurantOrderSearchRepository) {
        this.restaurantOrderRepository = restaurantOrderRepository;
        this.restaurantOrderSearchRepository = restaurantOrderSearchRepository;
    }

    /**
     * POST  /restaurant-orders : Create a new restaurantOrder.
     *
     * @param restaurantOrder the restaurantOrder to create
     * @return the ResponseEntity with status 201 (Created) and with body the new restaurantOrder, or with status 400 (Bad Request) if the restaurantOrder has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/restaurant-orders")
    @Timed
    public ResponseEntity<RestaurantOrder> createRestaurantOrder(@Valid @RequestBody RestaurantOrder restaurantOrder) throws URISyntaxException {
        log.debug("REST request to save RestaurantOrder : {}", restaurantOrder);
        if (restaurantOrder.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new restaurantOrder cannot already have an ID")).body(null);
        }
        RestaurantOrder result = restaurantOrderRepository.save(restaurantOrder);
        restaurantOrderSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/restaurant-orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /restaurant-orders : Updates an existing restaurantOrder.
     *
     * @param restaurantOrder the restaurantOrder to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated restaurantOrder,
     * or with status 400 (Bad Request) if the restaurantOrder is not valid,
     * or with status 500 (Internal Server Error) if the restaurantOrder couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/restaurant-orders")
    @Timed
    public ResponseEntity<RestaurantOrder> updateRestaurantOrder(@Valid @RequestBody RestaurantOrder restaurantOrder) throws URISyntaxException {
        log.debug("REST request to update RestaurantOrder : {}", restaurantOrder);
        if (restaurantOrder.getId() == null) {
            return createRestaurantOrder(restaurantOrder);
        }
        RestaurantOrder result = restaurantOrderRepository.save(restaurantOrder);
        restaurantOrderSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, restaurantOrder.getId().toString()))
            .body(result);
    }

    /**
     * GET  /restaurant-orders : get all the restaurantOrders.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of restaurantOrders in body
     */
    @GetMapping("/restaurant-orders")
    @Timed
    public ResponseEntity<List<RestaurantOrder>> getAllRestaurantOrders(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of RestaurantOrders");
        Page<RestaurantOrder> page = restaurantOrderRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/restaurant-orders");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /restaurant-orders/:id : get the "id" restaurantOrder.
     *
     * @param id the id of the restaurantOrder to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the restaurantOrder, or with status 404 (Not Found)
     */
    @GetMapping("/restaurant-orders/{id}")
    @Timed
    public ResponseEntity<RestaurantOrder> getRestaurantOrder(@PathVariable Long id) {
        log.debug("REST request to get RestaurantOrder : {}", id);
        RestaurantOrder restaurantOrder = restaurantOrderRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(restaurantOrder));
    }

    /**
     * DELETE  /restaurant-orders/:id : delete the "id" restaurantOrder.
     *
     * @param id the id of the restaurantOrder to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/restaurant-orders/{id}")
    @Timed
    public ResponseEntity<Void> deleteRestaurantOrder(@PathVariable Long id) {
        log.debug("REST request to delete RestaurantOrder : {}", id);
        restaurantOrderRepository.delete(id);
        restaurantOrderSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/restaurant-orders?query=:query : search for the restaurantOrder corresponding
     * to the query.
     *
     * @param query the query of the restaurantOrder search 
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/restaurant-orders")
    @Timed
    public ResponseEntity<List<RestaurantOrder>> searchRestaurantOrders(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of RestaurantOrders for query {}", query);
        Page<RestaurantOrder> page = restaurantOrderSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/restaurant-orders");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
