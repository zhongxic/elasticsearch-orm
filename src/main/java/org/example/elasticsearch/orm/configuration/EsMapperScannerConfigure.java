package org.example.elasticsearch.orm.configuration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;

import java.util.List;

public class EsMapperScannerConfigure implements BeanFactoryAware, BeanDefinitionRegistryPostProcessor {

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        // nothing to do here ...
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        List<String> basePackages = AutoConfigurationPackages.get(this.beanFactory);
        EsMapperScanner esMapperScanner = new EsMapperScanner(beanDefinitionRegistry);
        esMapperScanner.scan(basePackages.toArray(new String[0]));
    }

}
