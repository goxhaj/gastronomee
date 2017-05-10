package com.gastronomee.repository.search;

import com.gastronomee.domain.Dish;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Dish entity.
 */
public interface DishSearchRepository extends ElasticsearchRepository<Dish, Long> {
}
