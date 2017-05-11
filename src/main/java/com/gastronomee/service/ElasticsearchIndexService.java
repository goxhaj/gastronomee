package com.gastronomee.service;

import com.codahale.metrics.annotation.Timed;
import com.gastronomee.domain.*;
import com.gastronomee.repository.*;
import com.gastronomee.repository.search.*;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

@Service
public class ElasticsearchIndexService {

    private final Logger log = LoggerFactory.getLogger(ElasticsearchIndexService.class);

    private final CountryRepository countryRepository;

    private final CountrySearchRepository countrySearchRepository;

    private final DishRepository dishRepository;

    private final DishSearchRepository dishSearchRepository;

    private final DishCategoryRepository dishCategoryRepository;

    private final DishCategorySearchRepository dishCategorySearchRepository;

    private final IngredientRepository ingredientRepository;

    private final IngredientSearchRepository ingredientSearchRepository;

    private final LocationRepository locationRepository;

    private final LocationSearchRepository locationSearchRepository;

    private final MenuRepository menuRepository;

    private final MenuSearchRepository menuSearchRepository;

    private final RegionRepository regionRepository;

    private final RegionSearchRepository regionSearchRepository;

    private final RestaurantRepository restaurantRepository;

    private final RestaurantSearchRepository restaurantSearchRepository;

    private final UserRepository userRepository;

    private final UserSearchRepository userSearchRepository;

    private final ElasticsearchTemplate elasticsearchTemplate;

    public ElasticsearchIndexService(
        UserRepository userRepository,
        UserSearchRepository userSearchRepository,
        CountryRepository countryRepository,
        CountrySearchRepository countrySearchRepository,
        DishRepository dishRepository,
        DishSearchRepository dishSearchRepository,
        DishCategoryRepository dishCategoryRepository,
        DishCategorySearchRepository dishCategorySearchRepository,
        IngredientRepository ingredientRepository,
        IngredientSearchRepository ingredientSearchRepository,
        LocationRepository locationRepository,
        LocationSearchRepository locationSearchRepository,
        MenuRepository menuRepository,
        MenuSearchRepository menuSearchRepository,
        RegionRepository regionRepository,
        RegionSearchRepository regionSearchRepository,
        RestaurantRepository restaurantRepository,
        RestaurantSearchRepository restaurantSearchRepository,
        ElasticsearchTemplate elasticsearchTemplate) {
        this.userRepository = userRepository;
        this.userSearchRepository = userSearchRepository;
        this.countryRepository = countryRepository;
        this.countrySearchRepository = countrySearchRepository;
        this.dishRepository = dishRepository;
        this.dishSearchRepository = dishSearchRepository;
        this.dishCategoryRepository = dishCategoryRepository;
        this.dishCategorySearchRepository = dishCategorySearchRepository;
        this.ingredientRepository = ingredientRepository;
        this.ingredientSearchRepository = ingredientSearchRepository;
        this.locationRepository = locationRepository;
        this.locationSearchRepository = locationSearchRepository;
        this.menuRepository = menuRepository;
        this.menuSearchRepository = menuSearchRepository;
        this.regionRepository = regionRepository;
        this.regionSearchRepository = regionSearchRepository;
        this.restaurantRepository = restaurantRepository;
        this.restaurantSearchRepository = restaurantSearchRepository;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Async
    @Timed
    public void reindexAll() {
        reindexForClass(Country.class, countryRepository, countrySearchRepository);
        reindexForClass(Dish.class, dishRepository, dishSearchRepository);
        reindexForClass(DishCategory.class, dishCategoryRepository, dishCategorySearchRepository);
        reindexForClass(Ingredient.class, ingredientRepository, ingredientSearchRepository);
        reindexForClass(Location.class, locationRepository, locationSearchRepository);
        reindexForClass(Menu.class, menuRepository, menuSearchRepository);
        reindexForClass(Region.class, regionRepository, regionSearchRepository);
        reindexForClass(Restaurant.class, restaurantRepository, restaurantSearchRepository);
        reindexForClass(User.class, userRepository, userSearchRepository);

        log.info("Elasticsearch: Successfully performed reindexing");
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    private <T, ID extends Serializable> void reindexForClass(Class<T> entityClass, JpaRepository<T, ID> jpaRepository,
                                                              ElasticsearchRepository<T, ID> elasticsearchRepository) {
        elasticsearchTemplate.deleteIndex(entityClass);
        try {
            elasticsearchTemplate.createIndex(entityClass);
        } catch (IndexAlreadyExistsException e) {
            // Do nothing. Index was already concurrently recreated by some other service.
        }
        elasticsearchTemplate.putMapping(entityClass);
        if (jpaRepository.count() > 0) {
            try {
                Method m = jpaRepository.getClass().getMethod("findAllWithEagerRelationships");
                elasticsearchRepository.save((List<T>) m.invoke(jpaRepository));
            } catch (Exception e) {
                elasticsearchRepository.save(jpaRepository.findAll());
            }
        }
        log.info("Elasticsearch: Indexed all rows for " + entityClass.getSimpleName());
    }
}
