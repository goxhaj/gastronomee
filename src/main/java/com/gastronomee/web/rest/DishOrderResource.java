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
import com.gastronomee.domain.DishOrder;
import com.gastronomee.repository.DishOrderRepository;
import com.gastronomee.repository.search.DishOrderSearchRepository;
import com.gastronomee.web.rest.util.HeaderUtil;
import com.gastronomee.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing DishOrder.
 */
@RestController
@RequestMapping("/api")
public class DishOrderResource {

    private final Logger log = LoggerFactory.getLogger(DishOrderResource.class);

    private static final String ENTITY_NAME = "dishOrder";
        
    private final DishOrderRepository dishOrderRepository;

    private final DishOrderSearchRepository dishOrderSearchRepository;

    public DishOrderResource(DishOrderRepository dishOrderRepository, DishOrderSearchRepository dishOrderSearchRepository) {
        this.dishOrderRepository = dishOrderRepository;
        this.dishOrderSearchRepository = dishOrderSearchRepository;
    }

    /**
     * POST  /dish-orders : Create a new dishOrder.
     *
     * @param dishOrder the dishOrder to create
     * @return the ResponseEntity with status 201 (Created) and with body the new dishOrder, or with status 400 (Bad Request) if the dishOrder has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/dish-orders")
    @Timed
    public ResponseEntity<DishOrder> createDishOrder(@Valid @RequestBody DishOrder dishOrder) throws URISyntaxException {
        log.debug("REST request to save DishOrder : {}", dishOrder);
        if (dishOrder.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new dishOrder cannot already have an ID")).body(null);
        }
        DishOrder result = dishOrderRepository.save(dishOrder);
        dishOrderSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/dish-orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /dish-orders : Updates an existing dishOrder.
     *
     * @param dishOrder the dishOrder to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated dishOrder,
     * or with status 400 (Bad Request) if the dishOrder is not valid,
     * or with status 500 (Internal Server Error) if the dishOrder couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/dish-orders")
    @Timed
    public ResponseEntity<DishOrder> updateDishOrder(@Valid @RequestBody DishOrder dishOrder) throws URISyntaxException {
        log.debug("REST request to update DishOrder : {}", dishOrder);
        if (dishOrder.getId() == null) {
            return createDishOrder(dishOrder);
        }
        DishOrder result = dishOrderRepository.save(dishOrder);
        dishOrderSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, dishOrder.getId().toString()))
            .body(result);
    }

    /**
     * GET  /dish-orders : get all the dishOrders.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of dishOrders in body
     */
    @GetMapping("/dish-orders")
    @Timed
    public ResponseEntity<List<DishOrder>> getAllDishOrders(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of DishOrders");
        Page<DishOrder> page = dishOrderRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/dish-orders");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /dish-orders/:id : get the "id" dishOrder.
     *
     * @param id the id of the dishOrder to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the dishOrder, or with status 404 (Not Found)
     */
    @GetMapping("/dish-orders/{id}")
    @Timed
    public ResponseEntity<DishOrder> getDishOrder(@PathVariable Long id) {
        log.debug("REST request to get DishOrder : {}", id);
        DishOrder dishOrder = dishOrderRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(dishOrder));
    }

    /**
     * DELETE  /dish-orders/:id : delete the "id" dishOrder.
     *
     * @param id the id of the dishOrder to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/dish-orders/{id}")
    @Timed
    public ResponseEntity<Void> deleteDishOrder(@PathVariable Long id) {
        log.debug("REST request to delete DishOrder : {}", id);
        dishOrderRepository.delete(id);
        dishOrderSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/dish-orders?query=:query : search for the dishOrder corresponding
     * to the query.
     *
     * @param query the query of the dishOrder search 
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/dish-orders")
    @Timed
    public ResponseEntity<List<DishOrder>> searchDishOrders(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of DishOrders for query {}", query);
        Page<DishOrder> page = dishOrderSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/dish-orders");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
