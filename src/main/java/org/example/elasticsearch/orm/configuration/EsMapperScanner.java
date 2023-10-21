package org.example.elasticsearch.orm.configuration;

import org.example.elasticsearch.orm.annotation.EsMapper;
import org.example.elasticsearch.orm.spring.EsMapperFactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.util.Arrays;
import java.util.Set;

public class EsMapperScanner extends ClassPathBeanDefinitionScanner {

    public EsMapperScanner(BeanDefinitionRegistry registry) {
        super(registry);
        this.addIncludeFilter(new AnnotationTypeFilter(EsMapper.class));
    }

    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);

        if (beanDefinitions.isEmpty()) {
            logger.warn(String.format("no elasticsearch mapper found in '%s', please check your configuration", Arrays.toString(basePackages)));
        } else {
            postProcessBeanDefinitions(beanDefinitions);
        }

        return beanDefinitions;
    }

    private void postProcessBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
        for (BeanDefinitionHolder holder : beanDefinitions) {
            AbstractBeanDefinition definition = (AbstractBeanDefinition) holder.getBeanDefinition();
            Class<?> mapperInterface;
            try {
                mapperInterface = Class.forName(definition.getBeanClassName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("error post beanDefinition", e);
            }
            definition.setBeanClass(EsMapperFactoryBean.class);
            definition.getPropertyValues().add("mapperInterface", mapperInterface);
            definition.getPropertyValues().add("mapperFactory", new RuntimeBeanReference("mapperFactory"));
        }
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
    }

}
