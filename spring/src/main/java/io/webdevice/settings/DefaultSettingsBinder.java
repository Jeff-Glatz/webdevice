package io.webdevice.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.validation.DataBinder;

import static io.webdevice.wiring.WebDeviceScope.namespace;
import static java.util.Arrays.stream;
import static org.springframework.util.StringUtils.capitalize;

/**
 * This class allows the {@link io.webdevice.wiring.WebDeviceRuntime} to be used
 * in a core spring context where spring boot is not available.
 */
public class DefaultSettingsBinder
        implements SettingsBinder {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Settings from(ConfigurableEnvironment environment) {
        Settings settings = new Settings();
        DataBinder binder = new DataBinder(settings, "settings");
        binder.setConversionService(environment.getConversionService());
        binder.setAutoGrowNestedPaths(true);
        binder.bind(collectPropertyValues(environment));
        return settings;
    }

    private MutablePropertyValues collectPropertyValues(ConfigurableEnvironment environment) {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        String prefix = namespace("");
        environment.getPropertySources().stream()
                // Collect all webdevice specific properties
                .filter(EnumerablePropertySource.class::isInstance)
                .map(EnumerablePropertySource.class::cast)
                .flatMap(source -> stream(source.getPropertyNames()))
                .filter(name -> name.startsWith(prefix))
                .distinct()
                // Apply each property to the settings
                .forEach(environmentProperty -> {
                    String property = toCamelCase(environmentProperty.substring(prefix.length()));
                    log.info("Mapped environment property {} to {}", environmentProperty, property);
                    propertyValues.addPropertyValue(property,
                            environment.getProperty(environmentProperty));
                });
        return propertyValues;
    }

    static String toCamelCase(String value) {
        StringBuilder builder = new StringBuilder();
        for (String part : value.split("-")) {
            builder.append(builder.length() == 0
                    ? part :
                    capitalize(part));
        }
        return builder.toString();
    }
}
