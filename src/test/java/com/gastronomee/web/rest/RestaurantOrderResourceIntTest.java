package com.gastronomee.web.rest;

import com.gastronomee.GastronomeeApp;

import com.gastronomee.domain.RestaurantOrder;
import com.gastronomee.repository.RestaurantOrderRepository;
import com.gastronomee.repository.search.RestaurantOrderSearchRepository;
import com.gastronomee.web.rest.errors.ExceptionTranslator;

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

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.util.List;

import static com.gastronomee.web.rest.TestUtil.sameInstant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.gastronomee.domain.enumeration.RestaurantOrderStatus;
/**
 * Test class for the RestaurantOrderResource REST controller.
 *
 * @see RestaurantOrderResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GastronomeeApp.class)
public class RestaurantOrderResourceIntTest {

    private static final Integer DEFAULT_RATE = 1;
    private static final Integer UPDATED_RATE = 2;

    private static final Integer DEFAULT_PERSONS = 1;
    private static final Integer UPDATED_PERSONS = 2;

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_UPDATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final RestaurantOrderStatus DEFAULT_STATUS = RestaurantOrderStatus.OPENED;
    private static final RestaurantOrderStatus UPDATED_STATUS = RestaurantOrderStatus.CLOSED;

    @Autowired
    private RestaurantOrderRepository restaurantOrderRepository;

    @Autowired
    private RestaurantOrderSearchRepository restaurantOrderSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restRestaurantOrderMockMvc;

    private RestaurantOrder restaurantOrder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        RestaurantOrderResource restaurantOrderResource = new RestaurantOrderResource(restaurantOrderRepository, restaurantOrderSearchRepository);
        this.restRestaurantOrderMockMvc = MockMvcBuilders.standaloneSetup(restaurantOrderResource)
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
    public static RestaurantOrder createEntity(EntityManager em) {
        RestaurantOrder restaurantOrder = new RestaurantOrder()
            .rate(DEFAULT_RATE)
            .persons(DEFAULT_PERSONS)
            .comment(DEFAULT_COMMENT)
            .created(DEFAULT_CREATED)
            .updated(DEFAULT_UPDATED)
            .status(DEFAULT_STATUS);
        return restaurantOrder;
    }

    @Before
    public void initTest() {
        restaurantOrderSearchRepository.deleteAll();
        restaurantOrder = createEntity(em);
    }

    @Test
    @Transactional
    public void createRestaurantOrder() throws Exception {
        int databaseSizeBeforeCreate = restaurantOrderRepository.findAll().size();

        // Create the RestaurantOrder
        restRestaurantOrderMockMvc.perform(post("/api/restaurant-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(restaurantOrder)))
            .andExpect(status().isCreated());

        // Validate the RestaurantOrder in the database
        List<RestaurantOrder> restaurantOrderList = restaurantOrderRepository.findAll();
        assertThat(restaurantOrderList).hasSize(databaseSizeBeforeCreate + 1);
        RestaurantOrder testRestaurantOrder = restaurantOrderList.get(restaurantOrderList.size() - 1);
        assertThat(testRestaurantOrder.getRate()).isEqualTo(DEFAULT_RATE);
        assertThat(testRestaurantOrder.getPersons()).isEqualTo(DEFAULT_PERSONS);
        assertThat(testRestaurantOrder.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testRestaurantOrder.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testRestaurantOrder.getUpdated()).isEqualTo(DEFAULT_UPDATED);
        assertThat(testRestaurantOrder.getStatus()).isEqualTo(DEFAULT_STATUS);

        // Validate the RestaurantOrder in Elasticsearch
        RestaurantOrder restaurantOrderEs = restaurantOrderSearchRepository.findOne(testRestaurantOrder.getId());
        assertThat(restaurantOrderEs).isEqualToComparingFieldByField(testRestaurantOrder);
    }

    @Test
    @Transactional
    public void createRestaurantOrderWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = restaurantOrderRepository.findAll().size();

        // Create the RestaurantOrder with an existing ID
        restaurantOrder.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRestaurantOrderMockMvc.perform(post("/api/restaurant-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(restaurantOrder)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<RestaurantOrder> restaurantOrderList = restaurantOrderRepository.findAll();
        assertThat(restaurantOrderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkRateIsRequired() throws Exception {
        int databaseSizeBeforeTest = restaurantOrderRepository.findAll().size();
        // set the field null
        restaurantOrder.setRate(null);

        // Create the RestaurantOrder, which fails.

        restRestaurantOrderMockMvc.perform(post("/api/restaurant-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(restaurantOrder)))
            .andExpect(status().isBadRequest());

        List<RestaurantOrder> restaurantOrderList = restaurantOrderRepository.findAll();
        assertThat(restaurantOrderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkPersonsIsRequired() throws Exception {
        int databaseSizeBeforeTest = restaurantOrderRepository.findAll().size();
        // set the field null
        restaurantOrder.setPersons(null);

        // Create the RestaurantOrder, which fails.

        restRestaurantOrderMockMvc.perform(post("/api/restaurant-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(restaurantOrder)))
            .andExpect(status().isBadRequest());

        List<RestaurantOrder> restaurantOrderList = restaurantOrderRepository.findAll();
        assertThat(restaurantOrderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCommentIsRequired() throws Exception {
        int databaseSizeBeforeTest = restaurantOrderRepository.findAll().size();
        // set the field null
        restaurantOrder.setComment(null);

        // Create the RestaurantOrder, which fails.

        restRestaurantOrderMockMvc.perform(post("/api/restaurant-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(restaurantOrder)))
            .andExpect(status().isBadRequest());

        List<RestaurantOrder> restaurantOrderList = restaurantOrderRepository.findAll();
        assertThat(restaurantOrderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRestaurantOrders() throws Exception {
        // Initialize the database
        restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get all the restaurantOrderList
        restRestaurantOrderMockMvc.perform(get("/api/restaurant-orders?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(restaurantOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].rate").value(hasItem(DEFAULT_RATE)))
            .andExpect(jsonPath("$.[*].persons").value(hasItem(DEFAULT_PERSONS)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(sameInstant(DEFAULT_UPDATED))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    public void getRestaurantOrder() throws Exception {
        // Initialize the database
        restaurantOrderRepository.saveAndFlush(restaurantOrder);

        // Get the restaurantOrder
        restRestaurantOrderMockMvc.perform(get("/api/restaurant-orders/{id}", restaurantOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(restaurantOrder.getId().intValue()))
            .andExpect(jsonPath("$.rate").value(DEFAULT_RATE))
            .andExpect(jsonPath("$.persons").value(DEFAULT_PERSONS))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.created").value(sameInstant(DEFAULT_CREATED)))
            .andExpect(jsonPath("$.updated").value(sameInstant(DEFAULT_UPDATED)))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRestaurantOrder() throws Exception {
        // Get the restaurantOrder
        restRestaurantOrderMockMvc.perform(get("/api/restaurant-orders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRestaurantOrder() throws Exception {
        // Initialize the database
        restaurantOrderRepository.saveAndFlush(restaurantOrder);
        restaurantOrderSearchRepository.save(restaurantOrder);
        int databaseSizeBeforeUpdate = restaurantOrderRepository.findAll().size();

        // Update the restaurantOrder
        RestaurantOrder updatedRestaurantOrder = restaurantOrderRepository.findOne(restaurantOrder.getId());
        updatedRestaurantOrder
            .rate(UPDATED_RATE)
            .persons(UPDATED_PERSONS)
            .comment(UPDATED_COMMENT)
            .created(UPDATED_CREATED)
            .updated(UPDATED_UPDATED)
            .status(UPDATED_STATUS);

        restRestaurantOrderMockMvc.perform(put("/api/restaurant-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedRestaurantOrder)))
            .andExpect(status().isOk());

        // Validate the RestaurantOrder in the database
        List<RestaurantOrder> restaurantOrderList = restaurantOrderRepository.findAll();
        assertThat(restaurantOrderList).hasSize(databaseSizeBeforeUpdate);
        RestaurantOrder testRestaurantOrder = restaurantOrderList.get(restaurantOrderList.size() - 1);
        assertThat(testRestaurantOrder.getRate()).isEqualTo(UPDATED_RATE);
        assertThat(testRestaurantOrder.getPersons()).isEqualTo(UPDATED_PERSONS);
        assertThat(testRestaurantOrder.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testRestaurantOrder.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testRestaurantOrder.getUpdated()).isEqualTo(UPDATED_UPDATED);
        assertThat(testRestaurantOrder.getStatus()).isEqualTo(UPDATED_STATUS);

        // Validate the RestaurantOrder in Elasticsearch
        RestaurantOrder restaurantOrderEs = restaurantOrderSearchRepository.findOne(testRestaurantOrder.getId());
        assertThat(restaurantOrderEs).isEqualToComparingFieldByField(testRestaurantOrder);
    }

    @Test
    @Transactional
    public void updateNonExistingRestaurantOrder() throws Exception {
        int databaseSizeBeforeUpdate = restaurantOrderRepository.findAll().size();

        // Create the RestaurantOrder

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restRestaurantOrderMockMvc.perform(put("/api/restaurant-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(restaurantOrder)))
            .andExpect(status().isCreated());

        // Validate the RestaurantOrder in the database
        List<RestaurantOrder> restaurantOrderList = restaurantOrderRepository.findAll();
        assertThat(restaurantOrderList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteRestaurantOrder() throws Exception {
        // Initialize the database
        restaurantOrderRepository.saveAndFlush(restaurantOrder);
        restaurantOrderSearchRepository.save(restaurantOrder);
        int databaseSizeBeforeDelete = restaurantOrderRepository.findAll().size();

        // Get the restaurantOrder
        restRestaurantOrderMockMvc.perform(delete("/api/restaurant-orders/{id}", restaurantOrder.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean restaurantOrderExistsInEs = restaurantOrderSearchRepository.exists(restaurantOrder.getId());
        assertThat(restaurantOrderExistsInEs).isFalse();

        // Validate the database is empty
        List<RestaurantOrder> restaurantOrderList = restaurantOrderRepository.findAll();
        assertThat(restaurantOrderList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchRestaurantOrder() throws Exception {
        // Initialize the database
        restaurantOrderRepository.saveAndFlush(restaurantOrder);
        restaurantOrderSearchRepository.save(restaurantOrder);

        // Search the restaurantOrder
        restRestaurantOrderMockMvc.perform(get("/api/_search/restaurant-orders?query=id:" + restaurantOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(restaurantOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].rate").value(hasItem(DEFAULT_RATE)))
            .andExpect(jsonPath("$.[*].persons").value(hasItem(DEFAULT_PERSONS)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(sameInstant(DEFAULT_UPDATED))))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RestaurantOrder.class);
        RestaurantOrder restaurantOrder1 = new RestaurantOrder();
        restaurantOrder1.setId(1L);
        RestaurantOrder restaurantOrder2 = new RestaurantOrder();
        restaurantOrder2.setId(restaurantOrder1.getId());
        assertThat(restaurantOrder1).isEqualTo(restaurantOrder2);
        restaurantOrder2.setId(2L);
        assertThat(restaurantOrder1).isNotEqualTo(restaurantOrder2);
        restaurantOrder1.setId(null);
        assertThat(restaurantOrder1).isNotEqualTo(restaurantOrder2);
    }
}
