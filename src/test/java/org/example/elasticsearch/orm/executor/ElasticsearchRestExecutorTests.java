package org.example.elasticsearch.orm.executor;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
class ElasticsearchRestExecutorTests {

    private RestHighLevelClient restHighLevelClient;

    @BeforeEach
    public void setup() {
        restHighLevelClient = new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
    }

    @Test
    void query() {
        ElasticsearchRestHighLevelClientExecutor executor = new ElasticsearchRestHighLevelClientExecutor();
        executor.setRestHighLevelClient(restHighLevelClient);
        SearchResponse response = executor.query("user", "{\"query\":{\"match_all\":{}}}");
        Assertions.assertNotNull(response);
        log.info("response: {}", response);
    }

    @Test
    void count() {
        ElasticsearchRestHighLevelClientExecutor executor = new ElasticsearchRestHighLevelClientExecutor();
        executor.setRestHighLevelClient(restHighLevelClient);
        CountResponse response = executor.count("user", "{\"query\":{\"match_all\":{}}}");
        Assertions.assertNotNull(response);
        log.info("response: {}", response);
    }

}
