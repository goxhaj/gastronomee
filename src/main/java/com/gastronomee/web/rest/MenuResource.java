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
import com.gastronomee.domain.Menu;
import com.gastronomee.domain.Restaurant;
import com.gastronomee.repository.MenuRepository;
import com.gastronomee.repository.RestaurantRepository;
import com.gastronomee.repository.search.MenuSearchRepository;
import com.gastronomee.security.AuthoritiesConstants;
import com.gastronomee.security.SecurityUtils;
import com.gastronomee.web.rest.util.HeaderUtil;
import com.gastronomee.web.rest.util.PaginationUtil;

import io.github.jhipster.web.util.ResponseUtil;
import io.swagger.annotations.ApiParam;

/**
 * REST controller for managing Menu.
 */
@RestController
@RequestMapping("/api")
public class MenuResource {

    private final Logger log = LoggerFactory.getLogger(MenuResource.class);

    private static final String ENTITY_NAME = "menu";
    
    private final RestaurantRepository restaurantRepository;
        
    private final MenuRepository menuRepository;

    private final MenuSearchRepository menuSearchRepository;

    public MenuResource(MenuRepository menuRepository, MenuSearchRepository menuSearchRepository, RestaurantRepository restaurantRepository) {
        this.menuRepository = menuRepository;
        this.menuSearchRepository = menuSearchRepository;
        this.restaurantRepository=restaurantRepository;
    }

    /**
     * POST  /menus : Create a new menu.
     *
     * @param menu the menu to create
     * @return the ResponseEntity with status 201 (Created) and with body the new menu, or with status 400 (Bad Request) if the menu has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/menus")
    @Timed
    @Secured({
    	AuthoritiesConstants.ADMIN,
    	AuthoritiesConstants.MANAGER
    })
    public ResponseEntity<Menu> createMenu(@Valid @RequestBody Menu menu) throws URISyntaxException {
        log.debug("REST request to save Menu : {}", menu);
        if (menu.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new menu cannot already have an ID")).body(null);
        }
        Menu result = menuRepository.save(menu);
        menuSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/menus/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /menus : Updates an existing menu.
     *
     * @param menu the menu to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated menu,
     * or with status 400 (Bad Request) if the menu is not valid,
     * or with status 500 (Internal Server Error) if the menu couldnt be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/menus")
    @Timed
    @Secured({
    	AuthoritiesConstants.ADMIN,
    	AuthoritiesConstants.MANAGER
    })
    public ResponseEntity<Menu> updateMenu(@Valid @RequestBody Menu menu) throws URISyntaxException {
        log.debug("REST request to update Menu : {}", menu);
        if (menu.getId() == null) {
            return createMenu(menu);
        }
        Menu result = menuRepository.save(menu);
        menuSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, menu.getId().toString()))
            .body(result);
    }

    /**
     * GET  /menus : get all the menus.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of menus in body
     */
    @GetMapping("/menus")
    @Timed
    public ResponseEntity<List<Menu>> getAllMenus(@ApiParam Pageable pageable) {
        log.debug("REST request to get a page of Menus");
        
        Page<Menu> page;
        if (SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
        	page = menuRepository.findAll(pageable);//admin sees all menus
        } else {
        	List<Restaurant> restaurants = restaurantRepository.findByManagerIsCurrentUser();
        	page = menuRepository.findAllByRestaurantIn(restaurants, pageable);//managers do not see each other menus
        }
             
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/menus");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }
    
    /**
     * GET  /menus : get all my menus.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of menus in body
     */
    @GetMapping("/menus/my")
    @Timed
    @Secured({
    	AuthoritiesConstants.ADMIN,
    	AuthoritiesConstants.MANAGER
    })
    public ResponseEntity<List<Menu>> getMyMenus(@ApiParam Pageable pageable) {
        log.debug("REST request to get my Menus");
             
        List<Restaurant> restaurants = restaurantRepository.findByManagerIsCurrentUser();
        Page<Menu> page = menuRepository.findAllByRestaurantIn(restaurants, pageable);//managers do not see each other menus    
             
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/menus/my");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /menus/:id : get the "id" menu.
     *
     * @param id the id of the menu to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the menu, or with status 404 (Not Found)
     */
    @GetMapping("/menus/{id}")
    @Timed
    public ResponseEntity<Menu> getMenu(@PathVariable Long id) {
        log.debug("REST request to get Menu : {}", id);
        Menu menu = menuRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(menu));
    }

    /**
     * DELETE  /menus/:id : delete the "id" menu.
     *
     * @param id the id of the menu to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/menus/{id}")
    @Timed
    @Secured({
    	AuthoritiesConstants.ADMIN,
    	AuthoritiesConstants.MANAGER
    })
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        log.debug("REST request to delete Menu : {}", id);

        List<Restaurant> restaurants = restaurantRepository.findByManagerIsCurrentUser();
        List<Menu> menu = menuRepository.findAllByRestaurantIn(restaurants);//managers do not see each other menus  
        
        if (menu.stream().filter(m -> m.getId().equals(id)).count() > 0 || SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN)) {
            menuRepository.delete(id);
            menuSearchRepository.delete(id);
            return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        } else {//not allowed
            return ResponseEntity.status(403).headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
        }
        
    }

    /**
     * SEARCH  /_search/menus?query=:query : search for the menu corresponding
     * to the query.
     *
     * @param query the query of the menu search 
     * @param pageable the pagination information
     * @return the result of the search
     */
    @GetMapping("/_search/menus")
    @Timed
    public ResponseEntity<List<Menu>> searchMenus(@RequestParam String query, @ApiParam Pageable pageable) {
        log.debug("REST request to search for a page of Menus for query {}", query);
        Page<Menu> page = menuSearchRepository.search(queryStringQuery(query), pageable);
        HttpHeaders headers = PaginationUtil.generateSearchPaginationHttpHeaders(query, page, "/api/_search/menus");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }


}
