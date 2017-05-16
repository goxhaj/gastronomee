package com.gastronomee.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
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
import com.gastronomee.domain.Rating;
import com.gastronomee.domain.User;
import com.gastronomee.repository.RatingRepository;
import com.gastronomee.repository.UserRepository;
import com.gastronomee.repository.search.RatingSearchRepository;
import com.gastronomee.security.SecurityUtils;
import com.gastronomee.web.rest.util.HeaderUtil;
import com.gastronomee.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing Rating.
 */
@RestController
@RequestMapping("/api")
public class RatingResource {

    private final Logger log = LoggerFactory.getLogger(RatingResource.class);

    private static final String ENTITY_NAME = "rating";
        
    private final RatingRepository ratingRepository;

    private final RatingSearchRepository ratingSearchRepository;
    
    private final UserRepository userRepository;

    public RatingResource(RatingRepository ratingRepository, RatingSearchRepository ratingSearchRepository,
    		UserRepository userRepository) {
        this.ratingRepository = ratingRepository;
        this.ratingSearchRepository = ratingSearchRepository;
        this.userRepository = userRepository;
    }

    /**
     * POST  /ratings : Create a new rating.
     *
     * @param rating the rating to create
     * @return the ResponseEntity with status 201 (Created) and with body the new rating, or with status 400 (Bad Request) if the rating has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/ratings")
    @Timed
    public ResponseEntity<Rating> createRating(@Valid @RequestBody Rating rating) throws URISyntaxException {
        log.debug("REST request to save Rating : {}", rating);
        
        if (rating.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new rating cannot already have an ID")).body(null);
        }
        User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
        if (ratingRepository.findByRestaurantIdAndUserIsCurrentUser(rating.getRestaurant().getId(), user) != null && ratingRepository.findByRestaurantIdAndUserIsCurrentUser(rating.getRestaurant().getId(), user).size()>0) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "ratingexists", "Already reviewed!")).body(null);
        }
        
        //the time of creation of the rating
        rating.setCreated(ZonedDateTime.now());
        
        //the current user that is creating this review
        rating.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        Rating result = ratingRepository.save(rating);
        
        ratingSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/ratings/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /ratings : Updates an existing rating.
     *
     * @param rating the rating to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated rating,
     * or with status 400 (Bad Request) if the rating is not valid,
     * or with status 500 (Internal Server Error) if the rating couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/ratings")
    @Timed
    public ResponseEntity<Rating> updateRating(@Valid @RequestBody Rating rating) throws URISyntaxException {
        log.debug("REST request to update Rating : {}", rating);
        if (rating.getId() == null) {
            return createRating(rating);
        }
        
        //the time of update of the rating
        rating.setUpdated(ZonedDateTime.now());
        
        
        Rating result = ratingRepository.save(rating);
        ratingSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, rating.getId().toString()))
            .body(result);
    }

    /**
     * GET  /ratings : get all the ratings.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of ratings in body
     */
    @GetMapping("/ratings")
    @Timed
    public ResponseEntity<List<Rating>> getAllRatings(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Ratings");
        Page<Rating> page = ratingRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/ratings");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /ratings/:id : get the "id" rating.
     *
     * @param id the id of the rating to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the rating, or with status 404 (Not Found)
     */
    @GetMapping("/ratings/{id}")
    @Timed
    public ResponseEntity<Rating> getRating(@PathVariable Long id) {
        log.debug("REST request to get Rating : {}", id);
        Rating rating = ratingRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(rating));
    }

    /**
     * DELETE  /ratings/:id : delete the "id" rating.
     *
     * @param id the id of the rating to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/ratings/{id}")
    @Timed
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        log.debug("REST request to delete Rating : {}", id);
        ratingRepository.delete(id);
        ratingSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/ratings?query=:query : search for the rating corresponding
     * to the query.
     *
     * @param query the query of the rating search 
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/ratings")
    @Timed
    public ResponseEntity<List<Rating>> searchRatings(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Ratings for query {}", query);
        Page<Rating> page = ratingSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/ratings");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
