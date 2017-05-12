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
import com.gastronomee.domain.DishCategory;
import com.gastronomee.repository.DishCategoryRepository;
import com.gastronomee.repository.search.DishCategorySearchRepository;
import com.gastronomee.security.AuthoritiesConstants;
import com.gastronomee.web.rest.util.HeaderUtil;
import com.gastronomee.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing DishCategory.
 */
@RestController
@RequestMapping("/api")
public class DishCategoryResource {

    private final Logger log = LoggerFactory.getLogger(DishCategoryResource.class);

    private static final String ENTITY_NAME = "dishCategory";
        
    private final DishCategoryRepository dishCategoryRepository;

    private final DishCategorySearchRepository dishCategorySearchRepository;

    public DishCategoryResource(DishCategoryRepository dishCategoryRepository, DishCategorySearchRepository dishCategorySearchRepository) {
        this.dishCategoryRepository = dishCategoryRepository;
        this.dishCategorySearchRepository = dishCategorySearchRepository;
    }

    /**
     * POST  /dish-categories : Create a new dishCategory.
     *
     * @param dishCategory the dishCategory to create
     * @return the ResponseEntity with status 201 (Created) and with body the new dishCategory, or with status 400 (Bad Request) if the dishCategory has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/dish-categories")
    @Timed
    @Secured({
    	AuthoritiesConstants.ADMIN,
    })
    public ResponseEntity<DishCategory> createDishCategory(@Valid @RequestBody DishCategory dishCategory) throws URISyntaxException {
        log.debug("REST request to save DishCategory : {}", dishCategory);
        if (dishCategory.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new dishCategory cannot already have an ID")).body(null);
        }
        DishCategory result = dishCategoryRepository.save(dishCategory);
        dishCategorySearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/dish-categories/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /dish-categories : Updates an existing dishCategory.
     *
     * @param dishCategory the dishCategory to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated dishCategory,
     * or with status 400 (Bad Request) if the dishCategory is not valid,
     * or with status 500 (Internal Server Error) if the dishCategory couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/dish-categories")
    @Timed
    @Secured({
    	AuthoritiesConstants.ADMIN,
    })
    public ResponseEntity<DishCategory> updateDishCategory(@Valid @RequestBody DishCategory dishCategory) throws URISyntaxException {
        log.debug("REST request to update DishCategory : {}", dishCategory);
        if (dishCategory.getId() == null) {
            return createDishCategory(dishCategory);
        }
        DishCategory result = dishCategoryRepository.save(dishCategory);
        dishCategorySearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, dishCategory.getId().toString()))
            .body(result);
    }

    /**
     * GET  /dish-categories : get all the dishCategories.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of dishCategories in body
     */
    @GetMapping("/dish-categories")
    @Timed
    public ResponseEntity<List<DishCategory>> getAllDishCategories(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of DishCategories");
        Page<DishCategory> page = dishCategoryRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/dish-categories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /dish-categories/:id : get the "id" dishCategory.
     *
     * @param id the id of the dishCategory to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the dishCategory, or with status 404 (Not Found)
     */
    @GetMapping("/dish-categories/{id}")
    @Timed
    public ResponseEntity<DishCategory> getDishCategory(@PathVariable Long id) {
        log.debug("REST request to get DishCategory : {}", id);
        DishCategory dishCategory = dishCategoryRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(dishCategory));
    }

    /**
     * DELETE  /dish-categories/:id : delete the "id" dishCategory.
     *
     * @param id the id of the dishCategory to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/dish-categories/{id}")
    @Timed
    @Secured({
    	AuthoritiesConstants.ADMIN,
    })
    public ResponseEntity<Void> deleteDishCategory(@PathVariable Long id) {
        log.debug("REST request to delete DishCategory : {}", id);
        dishCategoryRepository.delete(id);
        dishCategorySearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/dish-categories?query=:query : search for the dishCategory corresponding
     * to the query.
     *
     * @param query the query of the dishCategory search 
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/dish-categories")
    @Timed
    public ResponseEntity<List<DishCategory>> searchDishCategories(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of DishCategories for query {}", query);
        Page<DishCategory> page = dishCategorySearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/dish-categories");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
