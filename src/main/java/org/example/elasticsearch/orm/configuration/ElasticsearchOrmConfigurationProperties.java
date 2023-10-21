package org.example.elasticsearch.orm.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "elasticsearch.orm")
public class ElasticsearchOrmConfigurationProperties {

    private String mapperLocations;

}
