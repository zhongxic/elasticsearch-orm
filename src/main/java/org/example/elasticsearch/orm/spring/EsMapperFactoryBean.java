package org.example.elasticsearch.orm.spring;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.elasticsearch.orm.proxy.MapperFactory;
import org.springframework.beans.factory.FactoryBean;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EsMapperFactoryBean<T> implements FactoryBean<T> {

    private Class<T> mapperInterface;

    private MapperFactory mapperFactory;

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public T getObject() {
        return this.mapperFactory.getMapper(mapperInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return this.mapperInterface;
    }

}
