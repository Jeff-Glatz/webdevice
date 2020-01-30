package io.webdevice.wiring;

import io.webdevice.support.AnnotationAttributes;
import io.webdevice.support.YamlPropertySourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

import static io.webdevice.support.AnnotationAttributes.attributesOf;
import static io.webdevice.wiring.WebDeviceScope.namespace;
import static org.springframework.util.StringUtils.isEmpty;

public class WebDeviceSettings
        implements ImportBeanDefinitionRegistrar {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ConfigurableEnvironment environment;
    private final ResourceLoader loader;
    private final PropertySourceFactory factory;

    @Autowired
    public WebDeviceSettings(Environment environment, ResourceLoader loader) {
        // Spring implodes when used directly in constructor
        this.environment = (ConfigurableEnvironment) environment;
        this.loader = loader;
        this.factory = new YamlPropertySourceFactory();
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        log.info("Exporting WebDevice settings ...");
        AnnotationAttributes attributes = attributesOf(EnableWebDevice.class, metadata);
        MutablePropertySources sources = environment.getPropertySources();
        // Export the settings to the environment
        sources.addFirst(attributes.valueOf("settings", String.class, (location) -> {
            Resource resource = loader.getResource(location);
            log.info("Exporting settings in {}", resource.getDescription());
            return factory.createPropertySource(location, new EncodedResource(resource));
        }));
        // Export the annotation values directly
        sources.addFirst(attributes.asPropertySource(
                entry -> !entry.getKey().equals("settings") && !isEmpty(entry.getValue()),
                entry -> namespace(entry.getKey()),
                Map.Entry::getValue));
        log.info("WebDevice settings exported.");
    }
}
