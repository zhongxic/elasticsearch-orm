package org.example.elasticsearch.orm.handler;

import lombok.Setter;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.example.elasticsearch.orm.core.Configuration;
import org.example.elasticsearch.orm.core.EsPage;
import org.example.elasticsearch.orm.core.EsPageStarter;
import org.example.elasticsearch.orm.proxy.MapperMethod;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Setter
public class DefaultResponseHandler implements SearchResponseHandler {

    private final Configuration configuration;

    public DefaultResponseHandler(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Object handle(SearchResponse searchResponse, MapperMethod method, Class<?> resultType) {
        if (SearchResponse.class.equals(method.getMethod().getReturnType())) {
            return searchResponse;
        } else if (EsPage.class.equals(method.getMethod().getReturnType())) {
            return handlerEsPageResult(searchResponse, resultType);
        } else if (method.isMultiReturn()) {
            return handleMultiReturnRespWithResultType(searchResponse, method, resultType);
        } else {
            return handleSingleReturnRespWithResultType(searchResponse, resultType);
        }
    }

    @SuppressWarnings("unchecked")
    private <E> EsPage<E> handlerEsPageResult(SearchResponse searchResponse, Class<?> resultType) {
        List<E> objs = Arrays.asList(searchResponse.getHits().getHits())
                .parallelStream()
                .map(hit -> (E) getObjectFromType(hit, resultType))
                .collect(Collectors.toList());
        EsPage<E> esPage = EsPageStarter.get();
        if (esPage == null) {
            throw new UnsupportedOperationException("please exec EsPageStarter::start(current, size) before query");
        }
        esPage.setTotal(searchResponse.getHits().getTotalHits());
        esPage.setRecords(objs);
        return esPage;
    }

    private Object handleMultiReturnRespWithResultType(SearchResponse searchResponse, MapperMethod method, Class<?> resultType) {
        SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits.length == 0) {
            return Collections.emptyList();
        }
        List<Object> objs = Arrays.asList(hits)
                .parallelStream()
                .map(hit -> getObjectFromType(hit, resultType))
                .collect(Collectors.toList());
        if (method.isArrayReturn()) {
            Object arr = Array.newInstance(resultType, hits.length);
            for (int i = 0; i < hits.length; i++) {
                Array.set(arr, i, objs.get(i));
            }
            return arr;
        } else {
            return objs;
        }
    }

    private Object handleSingleReturnRespWithResultType(SearchResponse searchResponse, Class<?> resultType) {
        SearchHit[] hits = searchResponse.getHits().getHits();
        if (hits.length > 1) {
            throw new UnsupportedOperationException("found multi hits in single return method");
        }

        return getObjectFromType(hits[0], resultType);
    }

    private Object getObjectFromType(SearchHit hit, Class<?> resultType) {
        if (Map.class.isAssignableFrom(resultType)) {
            return hit.getSourceAsMap();
        }

        try {
            return configuration.getObjectMapper().readValue(hit.getSourceAsString(), resultType);
        } catch (Exception e) {
            throw new UnsupportedOperationException(String.format("error create object use source: %s ", hit.getSourceAsString()), e);
        }
    }

}
