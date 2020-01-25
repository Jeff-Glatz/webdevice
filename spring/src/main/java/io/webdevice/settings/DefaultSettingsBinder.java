package io.webdevice.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;

import static io.webdevice.wiring.WebDeviceScope.namespace;
import static java.util.Arrays.stream;
import static org.springframework.util.StringUtils.capitalize;

// TODO: Needs work
public class DefaultSettingsBinder
        implements SettingsBinder {
    private final Logger log = LoggerFactory.getLogger(DefaultSettingsBinder.class);

    @Override
    public Settings from(ConfigurableEnvironment environment) {
        String prefix = namespace("");
        Settings settings = new Settings();
        BeanWrapper wrapper = new BeanWrapperImpl(settings);
        wrapper.setAutoGrowNestedPaths(true);
        environment.getPropertySources().stream()
                .filter(EnumerablePropertySource.class::isInstance)
                .map(EnumerablePropertySource.class::cast)
                .flatMap(source -> stream(source.getPropertyNames()))
                .filter(name -> name.startsWith(prefix))
                .distinct()
                .forEach(environmentProperty -> {
                    String property = toCamelCase(environmentProperty.substring(prefix.length()));
                    String value = environment.getProperty(environmentProperty);
                    log.info("Mapped environment property {} to {} with value {}", environmentProperty, property, value);
                    wrapper.setPropertyValue(property, value);
                });
        return settings;
    }

    private static String toCamelCase(String value) {
        return stream(value.split("-"))
                .collect(StringBuilder::new,
                        (builder, part) -> builder.append(builder.length() == 0
                                ? part :
                                capitalize(part)),
                        (a, b) -> {
                        })
                .toString();
    }
}
