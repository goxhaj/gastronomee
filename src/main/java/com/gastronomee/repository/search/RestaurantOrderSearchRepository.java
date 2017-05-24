package com.gastronomee.repository.search;

import com.gastronomee.domain.RestaurantOrder;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the RestaurantOrder entity.
 */
public interface RestaurantOrderSearchRepository extends ElasticsearchRepository<RestaurantOrder, Long> {
}
