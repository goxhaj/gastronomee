package com.gastronomee.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import com.gastronomee.GastronomeeApp;
import com.gastronomee.domain.Restaurant;
import com.gastronomee.domain.enumeration.DayOfWeek;
import com.gastronomee.repository.DishRepository;
import com.gastronomee.repository.LocationRepository;
import com.gastronomee.repository.MenuRepository;
import com.gastronomee.repository.RestaurantRepository;
import com.gastronomee.repository.UserRepository;
import com.gastronomee.repository.search.LocationSearchRepository;
import com.gastronomee.repository.search.RestaurantSearchRepository;
import com.gastronomee.web.rest.errors.ExceptionTranslator;
/**
 * Test class for the RestaurantResource REST controller.
 *
 * @see RestaurantResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GastronomeeApp.class)
public class RestaurantResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_OPEN = "AAAAAAAAAA";
    private static final String UPDATED_OPEN = "BBBBBBBBBB";

    private static final String DEFAULT_CLOSE = "AAAAAAAAAA";
    private static final String UPDATED_CLOSE = "BBBBBBBBBB";

    private static final Integer DEFAULT_TABLES = 1;
    private static final Integer UPDATED_TABLES = 2;

    private static final Integer DEFAULT_CHAIRS = 1;
    private static final Integer UPDATED_CHAIRS = 2;

    private static final DayOfWeek DEFAULT_DAY_OF_WEEK_CLOSED = DayOfWeek.MONDAY;
    private static final DayOfWeek UPDATED_DAY_OF_WEEK_CLOSED = DayOfWeek.TUESDAY;

    private static final Boolean DEFAULT_OPENED = false;
    private static final Boolean UPDATED_OPENED = true;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantSearchRepository restaurantSearchRepository;
    
    @Autowired
    private MenuRepository menuRepository;
    
    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private LocationSearchRepository locationSearchRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restRestaurantMockMvc;

    private Restaurant restaurant;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RestaurantResource restaurantResource = new RestaurantResource(restaurantRepository, restaurantSearchRepository, 
        		locationRepository, locationSearchRepository, menuRepository, userRepository, dishRepository);
        this.restRestaurantMockMvc = MockMvcBuilders.standaloneSetup(restaurantResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Restaurant createEntity(EntityManager em) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(DEFAULT_NAME);
        restaurant.setDescription(DEFAULT_DESCRIPTION);
        restaurant.setOpen(DEFAULT_OPEN);
        restaurant.setClose(DEFAULT_CLOSE);
        restaurant.setTables(DEFAULT_TABLES);
        restaurant.setChairs(DEFAULT_CHAIRS);
        restaurant.setDayOfWeekClosed(DEFAULT_DAY_OF_WEEK_CLOSED);
        restaurant.setOpened(DEFAULT_OPENED);
        return restaurant;
    }

    @Before
    public void initTest() {
        restaurantSearchRepository.deleteAll();
        restaurant = createEntity(em);
    }

    @Test
    @Transactional
    public void createRestaurant() throws Exception {
        int databaseSizeBeforeCreate = restaurantRepository.findAll().size();

        // Create the Restaurant
        restRestaurantMockMvc.perform(post("/api/restaurants")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(restaurant)))
            .andExpect(status().isCreated());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeCreate + 1);
        Restaurant testRestaurant = restaurantList.get(restaurantList.size() - 1);
        assertThat(testRestaurant.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testRestaurant.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testRestaurant.getOpen()).isEqualTo(DEFAULT_OPEN);
        assertThat(testRestaurant.getClose()).isEqualTo(DEFAULT_CLOSE);
        assertThat(testRestaurant.getTables()).isEqualTo(DEFAULT_TABLES);
        assertThat(testRestaurant.getChairs()).isEqualTo(DEFAULT_CHAIRS);
        assertThat(testRestaurant.getDayOfWeekClosed()).isEqualTo(DEFAULT_DAY_OF_WEEK_CLOSED);
        assertThat(testRestaurant.isOpened()).isEqualTo(DEFAULT_OPENED);

        // Validate the Restaurant in Elasticsearch
        Restaurant restaurantEs = restaurantSearchRepository.findOne(testRestaurant.getId());
        assertThat(restaurantEs).isEqualToComparingFieldByField(testRestaurant);
    }

    @Test
    @Transactional
    public void createRestaurantWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = restaurantRepository.findAll().size();

        // Create the Restaurant with an existing ID
        restaurant.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRestaurantMockMvc.perform(post("/api/restaurants")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(restaurant)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = restaurantRepository.findAll().size();
        // set the field null
        restaurant.setName(null);

        // Create the Restaurant, which fails.

        restRestaurantMockMvc.perform(post("/api/restaurants")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(restaurant)))
            .andExpect(status().isBadRequest());

        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkOpenIsRequired() throws Exception {
        int databaseSizeBeforeTest = restaurantRepository.findAll().size();
        // set the field null
        restaurant.setOpen(null);

        // Create the Restaurant, which fails.

        restRestaurantMockMvc.perform(post("/api/restaurants")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(restaurant)))
            .andExpect(status().isBadRequest());

        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCloseIsRequired() throws Exception {
        int databaseSizeBeforeTest = restaurantRepository.findAll().size();
        // set the field null
        restaurant.setClose(null);

        // Create the Restaurant, which fails.

        restRestaurantMockMvc.perform(post("/api/restaurants")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(restaurant)))
            .andExpect(status().isBadRequest());

        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRestaurants() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get all the restaurantList
        restRestaurantMockMvc.perform(get("/api/restaurants?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(restaurant.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].open").value(hasItem(DEFAULT_OPEN.toString())))
            .andExpect(jsonPath("$.[*].close").value(hasItem(DEFAULT_CLOSE.toString())))
            .andExpect(jsonPath("$.[*].tables").value(hasItem(DEFAULT_TABLES)))
            .andExpect(jsonPath("$.[*].chairs").value(hasItem(DEFAULT_CHAIRS)))
            .andExpect(jsonPath("$.[*].dayOfWeekClosed").value(hasItem(DEFAULT_DAY_OF_WEEK_CLOSED.toString())))
            .andExpect(jsonPath("$.[*].opened").value(hasItem(DEFAULT_OPENED.booleanValue())));
    }

    @Test
    @Transactional
    public void getRestaurant() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);

        // Get the restaurant
        restRestaurantMockMvc.perform(get("/api/restaurants/{id}", restaurant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(restaurant.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.open").value(DEFAULT_OPEN.toString()))
            .andExpect(jsonPath("$.close").value(DEFAULT_CLOSE.toString()))
            .andExpect(jsonPath("$.tables").value(DEFAULT_TABLES))
            .andExpect(jsonPath("$.chairs").value(DEFAULT_CHAIRS))
            .andExpect(jsonPath("$.dayOfWeekClosed").value(DEFAULT_DAY_OF_WEEK_CLOSED.toString()))
            .andExpect(jsonPath("$.opened").value(DEFAULT_OPENED.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingRestaurant() throws Exception {
        // Get the restaurant
        restRestaurantMockMvc.perform(get("/api/restaurants/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRestaurant() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);
        restaurantSearchRepository.save(restaurant);
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();

        // Update the restaurant
        Restaurant updatedRestaurant = restaurantRepository.findOne(restaurant.getId());
        updatedRestaurant.setName(UPDATED_NAME);
        updatedRestaurant.setDescription(UPDATED_DESCRIPTION);
        updatedRestaurant.setOpen(UPDATED_OPEN);
        updatedRestaurant.setClose(UPDATED_CLOSE);
        updatedRestaurant.setTables(UPDATED_TABLES);
        updatedRestaurant.setChairs(UPDATED_CHAIRS);
        updatedRestaurant.setDayOfWeekClosed(UPDATED_DAY_OF_WEEK_CLOSED);
        updatedRestaurant.setOpened(UPDATED_OPENED);

        restRestaurantMockMvc.perform(put("/api/restaurants")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedRestaurant)))
            .andExpect(status().isOk());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate);
        Restaurant testRestaurant = restaurantList.get(restaurantList.size() - 1);
        assertThat(testRestaurant.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testRestaurant.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testRestaurant.getOpen()).isEqualTo(UPDATED_OPEN);
        assertThat(testRestaurant.getClose()).isEqualTo(UPDATED_CLOSE);
        assertThat(testRestaurant.getTables()).isEqualTo(UPDATED_TABLES);
        assertThat(testRestaurant.getChairs()).isEqualTo(UPDATED_CHAIRS);
        assertThat(testRestaurant.getDayOfWeekClosed()).isEqualTo(UPDATED_DAY_OF_WEEK_CLOSED);
        assertThat(testRestaurant.isOpened()).isEqualTo(UPDATED_OPENED);

        // Validate the Restaurant in Elasticsearch
        Restaurant restaurantEs = restaurantSearchRepository.findOne(testRestaurant.getId());
        assertThat(restaurantEs).isEqualToComparingFieldByField(testRestaurant);
    }

    @Test
    @Transactional
    public void updateNonExistingRestaurant() throws Exception {
        int databaseSizeBeforeUpdate = restaurantRepository.findAll().size();

        // Create the Restaurant

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restRestaurantMockMvc.perform(put("/api/restaurants")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(restaurant)))
            .andExpect(status().isCreated());

        // Validate the Restaurant in the database
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteRestaurant() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);
        restaurantSearchRepository.save(restaurant);
        int databaseSizeBeforeDelete = restaurantRepository.findAll().size();

        // Get the restaurant
        restRestaurantMockMvc.perform(delete("/api/restaurants/{id}", restaurant.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean restaurantExistsInEs = restaurantSearchRepository.exists(restaurant.getId());
        assertThat(restaurantExistsInEs).isFalse();

        // Validate the database is empty
        List<Restaurant> restaurantList = restaurantRepository.findAll();
        assertThat(restaurantList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchRestaurant() throws Exception {
        // Initialize the database
        restaurantRepository.saveAndFlush(restaurant);
        restaurantSearchRepository.save(restaurant);

        // Search the restaurant
        restRestaurantMockMvc.perform(get("/api/_search/restaurants?query=id:" + restaurant.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(restaurant.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].open").value(hasItem(DEFAULT_OPEN.toString())))
            .andExpect(jsonPath("$.[*].close").value(hasItem(DEFAULT_CLOSE.toString())))
            .andExpect(jsonPath("$.[*].tables").value(hasItem(DEFAULT_TABLES)))
            .andExpect(jsonPath("$.[*].chairs").value(hasItem(DEFAULT_CHAIRS)))
            .andExpect(jsonPath("$.[*].dayOfWeekClosed").value(hasItem(DEFAULT_DAY_OF_WEEK_CLOSED.toString())))
            .andExpect(jsonPath("$.[*].opened").value(hasItem(DEFAULT_OPENED.booleanValue())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Restaurant.class);
    }
}
