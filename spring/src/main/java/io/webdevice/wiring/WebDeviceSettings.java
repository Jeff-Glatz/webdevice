package io.webdevice.wiring;

import io.webdevice.lang.annotation.Toggle;
import io.webdevice.support.AnnotationAttributes;
import io.webdevice.support.YamlPropertySourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.core.type.AnnotationMetadata;

import java.io.IOException;

import static io.webdevice.lang.annotation.Toggle.UNSET;
import static io.webdevice.support.AnnotationAttributes.attributesOf;
import static io.webdevice.wiring.WebDeviceScope.namespace;
import static java.lang.String.format;
import static org.springframework.util.StringUtils.isEmpty;

/**
 * This class is imported via {@link WebDeviceBootstrap} which is driven by the
 * {@link EnableWebDevice} annotation. Its purpose is to allow specific settings
 * to be exported to the current execution {@link ConfigurableEnvironment environment}
 * where it will be bound to a {@link io.webdevice.settings.Settings} instance by
 * the {@link WebDeviceRegistrar} for use in defining the runtime beans.
 *
 * @see EnableWebDevice
 * @see WebDeviceBootstrap
 * @see WebDeviceRegistrar
 */
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
        AnnotationAttributes attributes = attributesOf(EnableWebDevice.class, metadata);
        MutablePropertySources sources = environment.getPropertySources();
        if (attributes.hasValue("settings")) {
            String location = attributes.valueOf("settings");
            Resource resource = loader.getResource(location);
            try {
                log.info("Exporting settings from {} to the execution environment", resource.getDescription());
                sources.addFirst(factory.createPropertySource(location, new EncodedResource(resource)));
            } catch (IOException e) {
                throw new ApplicationContextException(
                        format("Failure creating PropertySource from %s", resource.getDescription()), e);
            }
        }
        log.info("Exporting settings from @EnableWebDevice {} to the execution environment", attributes.asMap());
        sources.addFirst(attributes.asPropertySource(
                // Exclude the settings and any empty or unset values
                entry -> !entry.getKey().equals("settings")
                        && !isEmpty(entry.getValue())
                        && entry.getValue() != UNSET,
                // Rewrite the keys to include the settings namespace
                entry -> namespace(entry.getKey()),
                // Convert annotation attribute values to strings
                entry -> entry.getValue() instanceof Class
                        ? ((Class<?>) entry.getValue()).getName()
                        : entry.getValue() instanceof Toggle
                        ? ((Toggle) entry.getValue()).toString()
                        : entry.getValue()));
        log.info("WebDevice settings exported.");
    }
}
