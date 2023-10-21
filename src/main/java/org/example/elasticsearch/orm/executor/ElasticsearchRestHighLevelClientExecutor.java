package org.example.elasticsearch.orm.executor;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.DeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.search.SearchModule;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.example.elasticsearch.orm.core.EsPageStarter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Getter
@Setter
public class ElasticsearchRestHighLevelClientExecutor implements ElasticsearchExecutor {

    private final SearchModule defaultSearchModule = new SearchModule(Settings.EMPTY, false, Collections.emptyList());

    private final NamedXContentRegistry defaultNamedXContentRegistry = new NamedXContentRegistry(this.defaultSearchModule.getNamedXContents());

    private RestHighLevelClient restHighLevelClient;

    @Override
    public SearchResponse query(String index, String dsl) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        try (XContentParser parser = XContentFactory.xContent(XContentType.JSON)
                .createParser(this.defaultNamedXContentRegistry, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, dsl)) {
            searchSourceBuilder.parseXContent(parser);
        } catch (IOException e) {
            throw new UnsupportedOperationException(String.format("error parse content: '%s' to SearchSource", dsl), e);
        }

        if (EsPageStarter.get() != null) {
            long current = EsPageStarter.get().getCurrent();
            long size = EsPageStarter.get().getSize();
            searchSourceBuilder.from((int) ((current - 1) * size));
            searchSourceBuilder.size((int) size);
        }

        log.info("execute query on index '{}' use searchSource: '{}'", index, searchSourceBuilder);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.source(searchSourceBuilder);
        try {
            return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(String.format("error execute query dsl: '%s' on index '%s'", dsl, index), e);
        }
    }

    @Override
    public CountResponse count(String index, String dsl) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        try (XContentParser parser = XContentFactory.xContent(XContentType.JSON)
                .createParser(this.defaultNamedXContentRegistry, DeprecationHandler.THROW_UNSUPPORTED_OPERATION, dsl)) {
            searchSourceBuilder.parseXContent(parser);
        } catch (IOException e) {
            throw new UnsupportedOperationException(String.format("error parse content: '%s' to SearchSource", dsl), e);
        }

        log.info("execute count on index '{}' use searchSource: '{}'", index, searchSourceBuilder);

        CountRequest countRequest = new CountRequest();
        countRequest.indices(index);
        countRequest.source(searchSourceBuilder);
        try {
            return restHighLevelClient.count(countRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(String.format("error execute count dsl: '%s' on index '%s'", dsl, index), e);
        }
    }
}
