package org.example.elasticsearch.orm.executor;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.core.CountResponse;

public interface ElasticsearchExecutor {

    SearchResponse query(String index, String dsl);

    CountResponse count(String index, String dsl);

}
