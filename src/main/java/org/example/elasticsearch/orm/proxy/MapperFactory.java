package org.example.elasticsearch.orm.proxy;

import org.example.elasticsearch.orm.core.Configuration;

import java.lang.reflect.Proxy;

public class MapperFactory {

    private final Configuration configuration;

    public MapperFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> type) {
        T mapper = configuration.getMapper(type);
        if (mapper != null) {
            return mapper;
        }
        mapper = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new MapperProxy(this.configuration));
        configuration.addMapper(type, mapper);
        return mapper;
    }

}
