package com.gastronomee.repository.search;

import com.gastronomee.domain.DishOrder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the DishOrder entity.
 */
public interface DishOrderSearchRepository extends ElasticsearchRepository<DishOrder, Long> {
}
