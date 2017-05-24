package com.gastronomee.web.rest;

import com.gastronomee.GastronomeeApp;

import com.gastronomee.domain.DishOrder;
import com.gastronomee.repository.DishOrderRepository;
import com.gastronomee.repository.search.DishOrderSearchRepository;
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

/**
 * Test class for the DishOrderResource REST controller.
 *
 * @see DishOrderResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GastronomeeApp.class)
public class DishOrderResourceIntTest {

    private static final Integer DEFAULT_RATE = 1;
    private static final Integer UPDATED_RATE = 2;

    private static final Integer DEFAULT_NR = 1;
    private static final Integer UPDATED_NR = 2;

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final ZonedDateTime DEFAULT_CREATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_CREATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    private static final ZonedDateTime DEFAULT_UPDATED = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0L), ZoneOffset.UTC);
    private static final ZonedDateTime UPDATED_UPDATED = ZonedDateTime.now(ZoneId.systemDefault()).withNano(0);

    @Autowired
    private DishOrderRepository dishOrderRepository;

    @Autowired
    private DishOrderSearchRepository dishOrderSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restDishOrderMockMvc;

    private DishOrder dishOrder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DishOrderResource dishOrderResource = new DishOrderResource(dishOrderRepository, dishOrderSearchRepository);
        this.restDishOrderMockMvc = MockMvcBuilders.standaloneSetup(dishOrderResource)
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
    public static DishOrder createEntity(EntityManager em) {
        DishOrder dishOrder = new DishOrder()
            .rate(DEFAULT_RATE)
            .nr(DEFAULT_NR)
            .comment(DEFAULT_COMMENT)
            .created(DEFAULT_CREATED)
            .updated(DEFAULT_UPDATED);
        return dishOrder;
    }

    @Before
    public void initTest() {
        dishOrderSearchRepository.deleteAll();
        dishOrder = createEntity(em);
    }

    @Test
    @Transactional
    public void createDishOrder() throws Exception {
        int databaseSizeBeforeCreate = dishOrderRepository.findAll().size();

        // Create the DishOrder
        restDishOrderMockMvc.perform(post("/api/dish-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dishOrder)))
            .andExpect(status().isCreated());

        // Validate the DishOrder in the database
        List<DishOrder> dishOrderList = dishOrderRepository.findAll();
        assertThat(dishOrderList).hasSize(databaseSizeBeforeCreate + 1);
        DishOrder testDishOrder = dishOrderList.get(dishOrderList.size() - 1);
        assertThat(testDishOrder.getRate()).isEqualTo(DEFAULT_RATE);
        assertThat(testDishOrder.getNr()).isEqualTo(DEFAULT_NR);
        assertThat(testDishOrder.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testDishOrder.getCreated()).isEqualTo(DEFAULT_CREATED);
        assertThat(testDishOrder.getUpdated()).isEqualTo(DEFAULT_UPDATED);

        // Validate the DishOrder in Elasticsearch
        DishOrder dishOrderEs = dishOrderSearchRepository.findOne(testDishOrder.getId());
        assertThat(dishOrderEs).isEqualToComparingFieldByField(testDishOrder);
    }

    @Test
    @Transactional
    public void createDishOrderWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = dishOrderRepository.findAll().size();

        // Create the DishOrder with an existing ID
        dishOrder.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDishOrderMockMvc.perform(post("/api/dish-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dishOrder)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<DishOrder> dishOrderList = dishOrderRepository.findAll();
        assertThat(dishOrderList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkRateIsRequired() throws Exception {
        int databaseSizeBeforeTest = dishOrderRepository.findAll().size();
        // set the field null
        dishOrder.setRate(null);

        // Create the DishOrder, which fails.

        restDishOrderMockMvc.perform(post("/api/dish-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dishOrder)))
            .andExpect(status().isBadRequest());

        List<DishOrder> dishOrderList = dishOrderRepository.findAll();
        assertThat(dishOrderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNrIsRequired() throws Exception {
        int databaseSizeBeforeTest = dishOrderRepository.findAll().size();
        // set the field null
        dishOrder.setNr(null);

        // Create the DishOrder, which fails.

        restDishOrderMockMvc.perform(post("/api/dish-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dishOrder)))
            .andExpect(status().isBadRequest());

        List<DishOrder> dishOrderList = dishOrderRepository.findAll();
        assertThat(dishOrderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkCommentIsRequired() throws Exception {
        int databaseSizeBeforeTest = dishOrderRepository.findAll().size();
        // set the field null
        dishOrder.setComment(null);

        // Create the DishOrder, which fails.

        restDishOrderMockMvc.perform(post("/api/dish-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dishOrder)))
            .andExpect(status().isBadRequest());

        List<DishOrder> dishOrderList = dishOrderRepository.findAll();
        assertThat(dishOrderList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDishOrders() throws Exception {
        // Initialize the database
        dishOrderRepository.saveAndFlush(dishOrder);

        // Get all the dishOrderList
        restDishOrderMockMvc.perform(get("/api/dish-orders?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dishOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].rate").value(hasItem(DEFAULT_RATE)))
            .andExpect(jsonPath("$.[*].nr").value(hasItem(DEFAULT_NR)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(sameInstant(DEFAULT_UPDATED))));
    }

    @Test
    @Transactional
    public void getDishOrder() throws Exception {
        // Initialize the database
        dishOrderRepository.saveAndFlush(dishOrder);

        // Get the dishOrder
        restDishOrderMockMvc.perform(get("/api/dish-orders/{id}", dishOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(dishOrder.getId().intValue()))
            .andExpect(jsonPath("$.rate").value(DEFAULT_RATE))
            .andExpect(jsonPath("$.nr").value(DEFAULT_NR))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.created").value(sameInstant(DEFAULT_CREATED)))
            .andExpect(jsonPath("$.updated").value(sameInstant(DEFAULT_UPDATED)));
    }

    @Test
    @Transactional
    public void getNonExistingDishOrder() throws Exception {
        // Get the dishOrder
        restDishOrderMockMvc.perform(get("/api/dish-orders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDishOrder() throws Exception {
        // Initialize the database
        dishOrderRepository.saveAndFlush(dishOrder);
        dishOrderSearchRepository.save(dishOrder);
        int databaseSizeBeforeUpdate = dishOrderRepository.findAll().size();

        // Update the dishOrder
        DishOrder updatedDishOrder = dishOrderRepository.findOne(dishOrder.getId());
        updatedDishOrder
            .rate(UPDATED_RATE)
            .nr(UPDATED_NR)
            .comment(UPDATED_COMMENT)
            .created(UPDATED_CREATED)
            .updated(UPDATED_UPDATED);

        restDishOrderMockMvc.perform(put("/api/dish-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedDishOrder)))
            .andExpect(status().isOk());

        // Validate the DishOrder in the database
        List<DishOrder> dishOrderList = dishOrderRepository.findAll();
        assertThat(dishOrderList).hasSize(databaseSizeBeforeUpdate);
        DishOrder testDishOrder = dishOrderList.get(dishOrderList.size() - 1);
        assertThat(testDishOrder.getRate()).isEqualTo(UPDATED_RATE);
        assertThat(testDishOrder.getNr()).isEqualTo(UPDATED_NR);
        assertThat(testDishOrder.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testDishOrder.getCreated()).isEqualTo(UPDATED_CREATED);
        assertThat(testDishOrder.getUpdated()).isEqualTo(UPDATED_UPDATED);

        // Validate the DishOrder in Elasticsearch
        DishOrder dishOrderEs = dishOrderSearchRepository.findOne(testDishOrder.getId());
        assertThat(dishOrderEs).isEqualToComparingFieldByField(testDishOrder);
    }

    @Test
    @Transactional
    public void updateNonExistingDishOrder() throws Exception {
        int databaseSizeBeforeUpdate = dishOrderRepository.findAll().size();

        // Create the DishOrder

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restDishOrderMockMvc.perform(put("/api/dish-orders")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dishOrder)))
            .andExpect(status().isCreated());

        // Validate the DishOrder in the database
        List<DishOrder> dishOrderList = dishOrderRepository.findAll();
        assertThat(dishOrderList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteDishOrder() throws Exception {
        // Initialize the database
        dishOrderRepository.saveAndFlush(dishOrder);
        dishOrderSearchRepository.save(dishOrder);
        int databaseSizeBeforeDelete = dishOrderRepository.findAll().size();

        // Get the dishOrder
        restDishOrderMockMvc.perform(delete("/api/dish-orders/{id}", dishOrder.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean dishOrderExistsInEs = dishOrderSearchRepository.exists(dishOrder.getId());
        assertThat(dishOrderExistsInEs).isFalse();

        // Validate the database is empty
        List<DishOrder> dishOrderList = dishOrderRepository.findAll();
        assertThat(dishOrderList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchDishOrder() throws Exception {
        // Initialize the database
        dishOrderRepository.saveAndFlush(dishOrder);
        dishOrderSearchRepository.save(dishOrder);

        // Search the dishOrder
        restDishOrderMockMvc.perform(get("/api/_search/dish-orders?query=id:" + dishOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dishOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].rate").value(hasItem(DEFAULT_RATE)))
            .andExpect(jsonPath("$.[*].nr").value(hasItem(DEFAULT_NR)))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].created").value(hasItem(sameInstant(DEFAULT_CREATED))))
            .andExpect(jsonPath("$.[*].updated").value(hasItem(sameInstant(DEFAULT_UPDATED))));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DishOrder.class);
        DishOrder dishOrder1 = new DishOrder();
        dishOrder1.setId(1L);
        DishOrder dishOrder2 = new DishOrder();
        dishOrder2.setId(dishOrder1.getId());
        assertThat(dishOrder1).isEqualTo(dishOrder2);
        dishOrder2.setId(2L);
        assertThat(dishOrder1).isNotEqualTo(dishOrder2);
        dishOrder1.setId(null);
        assertThat(dishOrder1).isNotEqualTo(dishOrder2);
    }
}
