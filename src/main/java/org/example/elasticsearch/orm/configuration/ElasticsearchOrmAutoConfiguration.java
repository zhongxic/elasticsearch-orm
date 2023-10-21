package org.example.elasticsearch.orm.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.client.RestHighLevelClient;
import org.example.elasticsearch.orm.executor.ElasticsearchExecutor;
import org.example.elasticsearch.orm.executor.ElasticsearchRestHighLevelClientExecutor;
import org.example.elasticsearch.orm.handler.DefaultResponseHandler;
import org.example.elasticsearch.orm.proxy.MapperFactory;
import org.example.elasticsearch.orm.xml.XmlStatementBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableConfigurationProperties({ElasticsearchOrmConfigurationProperties.class})
@Import(EsMapperScannerConfigure.class)
public class ElasticsearchOrmAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        return objectMapper;
    }

    @Bean
    @ConditionalOnMissingBean
    public ElasticsearchExecutor elasticsearchRestHighLevelClientExecutor(RestHighLevelClient restHighLevelClient) {
        ElasticsearchRestHighLevelClientExecutor executor = new ElasticsearchRestHighLevelClientExecutor();
        executor.setRestHighLevelClient(restHighLevelClient);
        return executor;
    }

    @Bean
    @ConditionalOnMissingBean
    public org.example.elasticsearch.orm.core.Configuration configuration(ElasticsearchOrmConfigurationProperties properties,
                                                                          ElasticsearchExecutor elasticsearchExecutor,
                                                                          ObjectMapper objectMapper)
            throws IOException {
        if (StringUtils.isBlank(properties.getMapperLocations())) {
            throw new IllegalArgumentException("the elasticsearch mapper locations is missing");
        }

        org.example.elasticsearch.orm.core.Configuration configuration = new org.example.elasticsearch.orm.core.Configuration();
        configuration.setElasticsearchExecutor(elasticsearchExecutor);
        configuration.setSearchResponseHandler(new DefaultResponseHandler(configuration));
        configuration.setObjectMapper(objectMapper);

        List<Resource> mapperResources = new ArrayList<>();
        PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
        String[] locations = StringUtils.split(properties.getMapperLocations(), ",");
        for (String location : locations) {
            Resource[] resources = pathMatchingResourcePatternResolver.getResources(location);
            mapperResources.addAll(Arrays.asList(resources));
        }
        mapperResources.parallelStream().forEach(
                resource -> {
                    try {
                        new XmlStatementBuilder(resource, configuration).parse();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });

        return configuration;
    }

    @Bean
    @ConditionalOnMissingBean
    public MapperFactory mapperFactory(org.example.elasticsearch.orm.core.Configuration configuration) {
        return new MapperFactory(configuration);
    }

}
