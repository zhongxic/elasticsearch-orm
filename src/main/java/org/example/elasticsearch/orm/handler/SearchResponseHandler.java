package org.example.elasticsearch.orm.handler;

import org.elasticsearch.action.search.SearchResponse;
import org.example.elasticsearch.orm.proxy.MapperMethod;

public interface SearchResponseHandler {

    Object handle(SearchResponse response, MapperMethod method, Class<?> resultType);

}
