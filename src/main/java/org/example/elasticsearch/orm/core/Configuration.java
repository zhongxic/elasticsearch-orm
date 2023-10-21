package org.example.elasticsearch.orm.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.example.elasticsearch.orm.executor.ElasticsearchExecutor;
import org.example.elasticsearch.orm.handler.SearchResponseHandler;
import org.springframework.core.convert.support.DefaultConversionService;

import java.util.concurrent.ConcurrentHashMap;

@Data
public class Configuration {

    private final ConcurrentHashMap<String, Statement> statements = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Class<?>, Object> mappers = new ConcurrentHashMap<>();

    private DefaultConversionService conversionService = new DefaultConversionService();

    private ElasticsearchExecutor elasticsearchExecutor;

    private SearchResponseHandler searchResponseHandler;

    private ObjectMapper objectMapper;

    public void addStatement(String id, Statement statement) {
        if (statements.containsKey(id)) {
            throw new IllegalArgumentException(String.format("statement with id '%s' has already exists", id));
        }
        statements.put(id, statement);
    }

    public Statement getStatement(String id) {
        return statements.get(id);
    }

    public <T> void addMapper(Class<T> type, T mapper) {
        if (mappers.containsKey(type)) {
            throw new IllegalArgumentException(String.format("mapper instance of type '%s' has already exists", type.getName()));
        }
        mappers.put(type, mapper);
    }

    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> type) {
        return (T) mappers.get(type);
    }

    public <T> T convertValue(Object value, Class<T> type) {
        if (value == null) {
            return null;
        }
        return conversionService.convert(value, type);
    }

}
