package io.webdevice.settings;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.bestquality.lang.CheckedFunction;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.validation.DataBinder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static io.webdevice.settings.DefaultSettingsBinder.toCamelCase;
import static io.webdevice.wiring.WebDeviceScope.namespace;
import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.util.ClassUtils.forName;

/**
 * This class allows the {@link io.webdevice.wiring.WebDeviceRuntime} to be configured
 * with a {@link Settings} loaded from a {@link ClassPathResource}.
 * <p>
 * This instance is activated when {@link io.webdevice.wiring.EnableWebDevice#settings()}
 * has been specified as an override to the settings normally bound from the execution
 * environment.
 *
 * <p>
 * The following resources types are supported:
 * <ul>
 *     <li>.json</li>
 *     <li>.properties</li>
 * </ul>
 */
public class SettingsFromResource
        implements CheckedFunction<String, Settings> {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final SettingsLoader defaultLoader = new UnsupportedLoader();
    private final Map<String, SettingsLoader> readers = new HashMap<>();
    private final ConfigurableEnvironment environment;

    public SettingsFromResource(ConfigurableEnvironment environment) {
        this.environment = environment;
        readers.put(".json", new JsonLoader());
        readers.put(".properties", new PropertiesLoader());
    }

    @Override
    public Settings apply(String path)
            throws Exception {
        SettingsLoader loader = readers.getOrDefault(
                path.substring(path.lastIndexOf('.')),
                defaultLoader);
        return loader.from(new ClassPathResource(path));
    }

    private interface SettingsLoader {
        Settings from(Resource resource)
                throws Exception;
    }

    private static class UnsupportedLoader
            implements SettingsLoader {

        @Override
        public Settings from(Resource resource) {
            throw new ApplicationContextException(format(
                    "Cannot load Settings from unsupported resource: %s", resource.getDescription()));
        }
    }

    private class JsonLoader
            implements SettingsLoader {

        @Override
        public Settings from(Resource resource)
                throws Exception {
            String content = environment.resolvePlaceholders(
                    IOUtils.toString(resource.getURL(), UTF_8));
            ObjectMapper mapper = new ObjectMapper()
                    .registerModule(new SimpleModule()
                            .addDeserializer(Class.class, new ClassDeserializer()));
            return mapper.convertValue(mapper.readTree(content)
                    .findValue("webdevice"), Settings.class);
        }

        public class ClassDeserializer
                extends StdDeserializer<Class<?>> {

            public ClassDeserializer() {
                super(Class.class);
            }

            @Override
            public Class<?> deserialize(JsonParser parser, DeserializationContext context)
                    throws IOException {
                try {
                    return forName(parser.readValueAs(String.class), null);
                } catch (ClassNotFoundException e) {
                    throw ValueInstantiationException.from(parser, "Failure deserializing Settings", e);
                }
            }
        }
    }

    private class PropertiesLoader
            implements SettingsLoader {

        @Override
        public Settings from(Resource resource)
                throws Exception {
            Properties properties = new Properties();
            properties.load(resource
                    .getURL()
                    .openStream());

            Settings settings = new Settings();
            DataBinder binder = new DataBinder(settings, "settings");
            binder.setConversionService(environment.getConversionService());
            binder.setAutoGrowNestedPaths(true);
            binder.bind(collectPropertyValues(properties));
            return settings;
        }

        private MutablePropertyValues collectPropertyValues(Properties properties) {
            MutablePropertyValues propertyValues = new MutablePropertyValues();
            String prefix = namespace("");
            properties.stringPropertyNames().stream()
                    // Collect all webdevice specific properties
                    .filter(name -> name.startsWith(prefix))
                    .distinct()
                    // Apply each property to the settings
                    .forEach(qualifiedProperty -> {
                        String property = toCamelCase(qualifiedProperty.substring(prefix.length()));
                        log.info("Mapped environment property {} to {}", qualifiedProperty, property);
                        propertyValues.addPropertyValue(property,
                                environment.resolvePlaceholders(properties.getProperty(qualifiedProperty)));
                    });
            return propertyValues;
        }
    }
}
