package com.gastronomee.web.rest;

import com.gastronomee.GastronomeeApp;

import com.gastronomee.domain.DishCategory;
import com.gastronomee.repository.DishCategoryRepository;
import com.gastronomee.repository.UserRepository;
import com.gastronomee.repository.search.DishCategorySearchRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the DishCategoryResource REST controller.
 *
 * @see DishCategoryResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GastronomeeApp.class)
public class DishCategoryResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Integer DEFAULT_PRIORITY = 1;
    private static final Integer UPDATED_PRIORITY = 2;

    @Autowired
    private DishCategoryRepository dishCategoryRepository;

    @Autowired
    private DishCategorySearchRepository dishCategorySearchRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restDishCategoryMockMvc;

    private DishCategory dishCategory;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DishCategoryResource dishCategoryResource = new DishCategoryResource(dishCategoryRepository, dishCategorySearchRepository, userRepository);
        this.restDishCategoryMockMvc = MockMvcBuilders.standaloneSetup(dishCategoryResource)
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
    public static DishCategory createEntity(EntityManager em) {
        DishCategory dishCategory = new DishCategory();
        dishCategory.setName(DEFAULT_NAME);
        dishCategory.setDescription(DEFAULT_DESCRIPTION);
        dishCategory.setActive(DEFAULT_ACTIVE);
        dishCategory.setPriority(DEFAULT_PRIORITY);
        return dishCategory;
    }

    @Before
    public void initTest() {
        dishCategorySearchRepository.deleteAll();
        dishCategory = createEntity(em);
    }

    @Test
    @Transactional
    public void createDishCategory() throws Exception {
        int databaseSizeBeforeCreate = dishCategoryRepository.findAll().size();

        // Create the DishCategory
        restDishCategoryMockMvc.perform(post("/api/dish-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dishCategory)))
            .andExpect(status().isCreated());

        // Validate the DishCategory in the database
        List<DishCategory> dishCategoryList = dishCategoryRepository.findAll();
        assertThat(dishCategoryList).hasSize(databaseSizeBeforeCreate + 1);
        DishCategory testDishCategory = dishCategoryList.get(dishCategoryList.size() - 1);
        assertThat(testDishCategory.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testDishCategory.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testDishCategory.isActive()).isEqualTo(DEFAULT_ACTIVE);
        assertThat(testDishCategory.getPriority()).isEqualTo(DEFAULT_PRIORITY);

        // Validate the DishCategory in Elasticsearch
        DishCategory dishCategoryEs = dishCategorySearchRepository.findOne(testDishCategory.getId());
        assertThat(dishCategoryEs).isEqualToComparingFieldByField(testDishCategory);
    }

    @Test
    @Transactional
    public void createDishCategoryWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = dishCategoryRepository.findAll().size();

        // Create the DishCategory with an existing ID
        dishCategory.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDishCategoryMockMvc.perform(post("/api/dish-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dishCategory)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<DishCategory> dishCategoryList = dishCategoryRepository.findAll();
        assertThat(dishCategoryList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = dishCategoryRepository.findAll().size();
        // set the field null
        dishCategory.setName(null);

        // Create the DishCategory, which fails.

        restDishCategoryMockMvc.perform(post("/api/dish-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dishCategory)))
            .andExpect(status().isBadRequest());

        List<DishCategory> dishCategoryList = dishCategoryRepository.findAll();
        assertThat(dishCategoryList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDishCategories() throws Exception {
        // Initialize the database
        dishCategoryRepository.saveAndFlush(dishCategory);

        // Get all the dishCategoryList
        restDishCategoryMockMvc.perform(get("/api/dish-categories?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dishCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY)));
    }

    @Test
    @Transactional
    public void getDishCategory() throws Exception {
        // Initialize the database
        dishCategoryRepository.saveAndFlush(dishCategory);

        // Get the dishCategory
        restDishCategoryMockMvc.perform(get("/api/dish-categories/{id}", dishCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(dishCategory.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE.booleanValue()))
            .andExpect(jsonPath("$.priority").value(DEFAULT_PRIORITY));
    }

    @Test
    @Transactional
    public void getNonExistingDishCategory() throws Exception {
        // Get the dishCategory
        restDishCategoryMockMvc.perform(get("/api/dish-categories/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDishCategory() throws Exception {
        // Initialize the database
        dishCategoryRepository.saveAndFlush(dishCategory);
        dishCategorySearchRepository.save(dishCategory);
        int databaseSizeBeforeUpdate = dishCategoryRepository.findAll().size();

        // Update the dishCategory
        DishCategory updatedDishCategory = dishCategoryRepository.findOne(dishCategory.getId());
        updatedDishCategory.setName(UPDATED_NAME);
        updatedDishCategory.setDescription(UPDATED_DESCRIPTION);
        updatedDishCategory.setActive(UPDATED_ACTIVE);
        updatedDishCategory.setPriority(UPDATED_PRIORITY);

        restDishCategoryMockMvc.perform(put("/api/dish-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedDishCategory)))
            .andExpect(status().isOk());

        // Validate the DishCategory in the database
        List<DishCategory> dishCategoryList = dishCategoryRepository.findAll();
        assertThat(dishCategoryList).hasSize(databaseSizeBeforeUpdate);
        DishCategory testDishCategory = dishCategoryList.get(dishCategoryList.size() - 1);
        assertThat(testDishCategory.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testDishCategory.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testDishCategory.isActive()).isEqualTo(UPDATED_ACTIVE);
        assertThat(testDishCategory.getPriority()).isEqualTo(UPDATED_PRIORITY);

        // Validate the DishCategory in Elasticsearch
        DishCategory dishCategoryEs = dishCategorySearchRepository.findOne(testDishCategory.getId());
        assertThat(dishCategoryEs).isEqualToComparingFieldByField(testDishCategory);
    }

    @Test
    @Transactional
    public void updateNonExistingDishCategory() throws Exception {
        int databaseSizeBeforeUpdate = dishCategoryRepository.findAll().size();

        // Create the DishCategory

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restDishCategoryMockMvc.perform(put("/api/dish-categories")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dishCategory)))
            .andExpect(status().isCreated());

        // Validate the DishCategory in the database
        List<DishCategory> dishCategoryList = dishCategoryRepository.findAll();
        assertThat(dishCategoryList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteDishCategory() throws Exception {
        // Initialize the database
        dishCategoryRepository.saveAndFlush(dishCategory);
        dishCategorySearchRepository.save(dishCategory);
        int databaseSizeBeforeDelete = dishCategoryRepository.findAll().size();

        // Get the dishCategory
        restDishCategoryMockMvc.perform(delete("/api/dish-categories/{id}", dishCategory.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean dishCategoryExistsInEs = dishCategorySearchRepository.exists(dishCategory.getId());
        assertThat(dishCategoryExistsInEs).isFalse();

        // Validate the database is empty
        List<DishCategory> dishCategoryList = dishCategoryRepository.findAll();
        assertThat(dishCategoryList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchDishCategory() throws Exception {
        // Initialize the database
        dishCategoryRepository.saveAndFlush(dishCategory);
        dishCategorySearchRepository.save(dishCategory);

        // Search the dishCategory
        restDishCategoryMockMvc.perform(get("/api/_search/dish-categories?query=id:" + dishCategory.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dishCategory.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE.booleanValue())))
            .andExpect(jsonPath("$.[*].priority").value(hasItem(DEFAULT_PRIORITY)));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DishCategory.class);
    }
}
