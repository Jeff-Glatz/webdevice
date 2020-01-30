package io.webdevice.support;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.support.EncodedResource;

import java.util.Properties;

public class YamlPropertySource
        extends PropertiesPropertySource {

    public YamlPropertySource(String name, EncodedResource resource) {
        super(name, from(resource));
    }

    private static Properties from(EncodedResource resource) {
        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(resource.getResource());
        factory.afterPropertiesSet();
        return factory.getObject();
    }
}
