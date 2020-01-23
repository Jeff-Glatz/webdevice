package io.webdevice.support;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;

public class YamlPropertySourceFactory
        implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource)
            throws IOException {
        Resource resource = encodedResource.getResource();
        if (name == null) {
            name = resource.getFile().getName();
        }
        CompositePropertySource propertySource = new CompositePropertySource(name);
        new YamlPropertySourceLoader()
                .load(resource.getFilename(), resource)
                .forEach(propertySource::addPropertySource);
        return propertySource;
    }
}
