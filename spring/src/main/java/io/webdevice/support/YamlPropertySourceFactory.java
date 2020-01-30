package io.webdevice.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;

import static java.lang.String.format;
import static org.springframework.util.ClassUtils.isPresent;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * Use this in conjunction with {@link org.springframework.context.annotation.PropertySource#factory()}
 * to load a specific webdevice {@link io.webdevice.settings.Settings} instance from a YAML file.
 */
public class YamlPropertySourceFactory
        extends DefaultPropertySourceFactory {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource)
            throws IOException {
        String filename = resource.getResource().getFilename();
        if (!isEmpty(filename) && (filename.endsWith(".yml") || filename.endsWith(".yaml"))) {
            if (!isPresent("org.yaml.snakeyaml.Yaml", null)) {
                throw new IllegalStateException(format("Attempted to load %s but " +
                        "snakeyaml was not found on the classpath", resource));
            }
            log.info("Loading YamlPropertySource from {}", resource.getResource().getDescription());
            return new YamlPropertySource(isEmpty(name) ? filename : name, resource);
        }
        return super.createPropertySource(name, resource);
    }
}
