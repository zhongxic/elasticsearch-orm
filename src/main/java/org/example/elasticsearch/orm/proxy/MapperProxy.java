package org.example.elasticsearch.orm.proxy;

import org.example.elasticsearch.orm.core.Configuration;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class MapperProxy implements InvocationHandler {

    private final Configuration configuration;

    private final ConcurrentHashMap<Method, MapperMethod> mapperMethods = new ConcurrentHashMap<>();

    public MapperProxy(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class.equals(method.getDeclaringClass())) {
            return method.invoke(this, args);
        }
        return mapperMethods.computeIfAbsent(method, m -> new MapperMethod(m, configuration))
                .execute(args);
    }

}
