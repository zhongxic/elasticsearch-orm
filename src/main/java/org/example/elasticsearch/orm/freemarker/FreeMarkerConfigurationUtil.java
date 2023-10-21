package org.example.elasticsearch.orm.freemarker;

import freemarker.template.Configuration;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FreeMarkerConfigurationUtil {

    private static volatile Configuration configuration;

    public static Configuration getConfiguration() {
        if (configuration == null) {
            synchronized (FreeMarkerConfigurationUtil.class) {
                if (configuration == null) {
                    Configuration config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
                    config.setSharedVariable("trim", new TrimDirective());
                    configuration = config;
                }
            }
        }
        return configuration;
    }

}
