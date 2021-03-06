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
import com.gastronomee.domain.Ingredient;
import com.gastronomee.repository.IngredientRepository;
import com.gastronomee.repository.UserRepository;
import com.gastronomee.repository.search.IngredientSearchRepository;
import com.gastronomee.security.AuthoritiesConstants;
import com.gastronomee.security.SecurityUtils;
import com.gastronomee.web.rest.util.HeaderUtil;
import com.gastronomee.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing Ingredient.
 */
@RestController
@RequestMapping("/api")
public class IngredientResource {

    private final Logger log = LoggerFactory.getLogger(IngredientResource.class);

    private static final String ENTITY_NAME = "ingredient";
        
    private final IngredientRepository ingredientRepository;

    private final IngredientSearchRepository ingredientSearchRepository;
    
    private final UserRepository userRepository;

    public IngredientResource(IngredientRepository ingredientRepository, IngredientSearchRepository ingredientSearchRepository, UserRepository userRepository) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientSearchRepository = ingredientSearchRepository;
        this.userRepository = userRepository;
    }
    
    /**
     * GET  /ingredients/name -> get all ingredients by the "name" partial .
     */
    @GetMapping("/ingredients/name")
    @Timed
    public ResponseEntity<List<Ingredient>> getIngredientsByName(@RequestParam(value = "name" , required = false) String name)
        throws URISyntaxException {
    	
		List<Ingredient> ingredients = ingredientRepository.findByNameIgnoreCaseContainingAndActiveTrue(name);
		return new ResponseEntity<List<Ingredient>>(ingredients, HttpStatus.OK);
		
    }
    
    /**
     * GET  /dish-categories/my : get all my dish categories.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of restaurants in body
     */
    @GetMapping("/ingredients/my")
    @Timed
    @Secured({
    	AuthoritiesConstants.ADMIN,
    	AuthoritiesConstants.MANAGER,
    })
    public ResponseEntity<List<Ingredient>> getMyDishes(@ApiParam Pageable pageable) {
        log.debug("REST request to get my DishCategories");
        Page<Ingredient> page = ingredientRepository.findByUserIsCurrentUser(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/ingredients/my");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * POST  /ingredients : Create a new ingredient.
     *
     * @param ingredient the ingredient to create
     * @return the ResponseEntity with status 201 (Created) and with body the new ingredient, or with status 400 (Bad Request) if the ingredient has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/ingredients")
    @Timed
    @Secured({
    	AuthoritiesConstants.MANAGER,
    	AuthoritiesConstants.ADMIN,
    })
    public ResponseEntity<Ingredient> createIngredient(@Valid @RequestBody Ingredient ingredient) throws URISyntaxException {
        log.debug("REST request to save Ingredient : {}", ingredient);
        if (ingredient.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new ingredient cannot already have an ID")).body(null);
        }
        
        //the current user that is creating this dish ingredient
        ingredient.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
        
        Ingredient result = ingredientRepository.save(ingredient);
        ingredientSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/ingredients/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /ingredients : Updates an existing ingredient.
     *
     * @param ingredient the ingredient to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated ingredient,
     * or with status 400 (Bad Request) if the ingredient is not valid,
     * or with status 500 (Internal Server Error) if the ingredient couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/ingredients")
    @Timed
    @Secured({
    	AuthoritiesConstants.MANAGER,
    	AuthoritiesConstants.ADMIN,
    })
    public ResponseEntity<Ingredient> updateIngredient(@Valid @RequestBody Ingredient ingredient) throws URISyntaxException {
        log.debug("REST request to update Ingredient : {}", ingredient);
        if (ingredient.getId() == null) {
            return createIngredient(ingredient);
        }
        
        if (!hasPermission(ingredient.getId())) {
        	return ResponseEntity.status(401)//access denied
        			.headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, ingredient.getId().toString())).build();
        } 
        
        Ingredient result = ingredientRepository.save(ingredient);
        ingredientSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, ingredient.getId().toString()))
            .body(result);
    }
    

    /**
     * GET  /ingredients : get all the ingredients.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of ingredients in body
     */
    @GetMapping("/ingredients")
    @Timed
    public ResponseEntity<List<Ingredient>> getAllIngredients(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Ingredients");
        Page<Ingredient> page = ingredientRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/ingredients");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /ingredients/:id : get the "id" ingredient.
     *
     * @param id the id of the ingredient to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the ingredient, or with status 404 (Not Found)
     */
    @GetMapping("/ingredients/{id}")
    @Timed
    public ResponseEntity<Ingredient> getIngredient(@PathVariable Long id) {
        log.debug("REST request to get Ingredient : {}", id);
        Ingredient ingredient = ingredientRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(ingredient));
    }

    /**
     * DELETE  /ingredients/:id : delete the "id" ingredient.
     *
     * @param id the id of the ingredient to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/ingredients/{id}")
    @Timed
    @Secured({
    	AuthoritiesConstants.MANAGER,
    	AuthoritiesConstants.ADMIN,
    })
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        log.debug("REST request to delete Ingredient : {}", id);
        
        if (!hasPermission(id)) {
        	return ResponseEntity.status(401)//access denied
        			.headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } 
        
        ingredientRepository.delete(id);
        ingredientSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/ingredients?query=:query : search for the ingredient corresponding
     * to the query.
     *
     * @param query the query of the ingredient search 
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/ingredients")
    @Timed
    public ResponseEntity<List<Ingredient>> searchIngredients(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Ingredients for query {}", query);
        Page<Ingredient> page = ingredientSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/ingredients");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    private boolean hasPermission(Long id){
    	if(ingredientRepository.findOneByUserLoginAndId(SecurityUtils.getCurrentUserLogin(), id) !=null || SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)){
    		return true;
    	} else {
    		log.warn("Permission denied of User with ID: {}", id);
    		return false;
    	}
    }


}
