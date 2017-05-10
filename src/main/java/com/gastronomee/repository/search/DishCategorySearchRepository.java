package com.gastronomee.repository.search;

import com.gastronomee.domain.DishCategory;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the DishCategory entity.
 */
public interface DishCategorySearchRepository extends ElasticsearchRepository<DishCategory, Long> {
}
