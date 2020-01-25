package io.webdevice.support;

import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.CompositePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * Use this in conjunction with {@link org.springframework.context.annotation.PropertySource#factory()}
 * to load a specific webdevice {@link io.webdevice.settings.Settings} instance from a YAML file.
 */
public class YamlPropertySourceFactory
        extends DefaultPropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource)
            throws IOException {
        Resource resource = encodedResource.getResource();
        String fileName = resource.getFilename();
        if (isEmpty(name)) {
            name = fileName;
        }
        if (fileName.endsWith(".yml") || fileName.endsWith(".yaml")) {
            CompositePropertySource propertySource = new CompositePropertySource(name);
            new YamlPropertySourceLoader()
                    .load(resource.getFilename(), resource)
                    .forEach(propertySource::addPropertySource);
            return propertySource;
        }
        return super.createPropertySource(name, encodedResource);
    }
}
