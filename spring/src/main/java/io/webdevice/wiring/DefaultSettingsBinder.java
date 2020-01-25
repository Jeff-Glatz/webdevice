package io.webdevice.wiring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;

import java.net.URL;
import java.util.Set;

import static io.webdevice.wiring.Settings.defaultScope;
import static io.webdevice.wiring.WebDeviceScope.namespace;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toSet;
import static org.springframework.util.StringUtils.capitalize;

// TODO: Needs work
public class DefaultSettingsBinder
        implements SettingsBinder {
    public static final Class<DefaultSettingsBinder> IMPL = null;

    @Override
    public Settings from(ConfigurableEnvironment environment) {
        final Logger log = LoggerFactory.getLogger(WebDeviceRegistrar.class);
        Settings settings = new Settings()
                .withScope(environment.getProperty(namespace("scope"), defaultScope()))
                .withDefaultDevice(environment.getProperty(namespace("default-device")))
                .withEager(environment.getProperty(namespace("eager"), Boolean.class, false))
                .withStrict(environment.getProperty(namespace("strict"), Boolean.class, true))
                .withBaseUrl(environment.getProperty(namespace("base-url"), URL.class, null));

        Set<String> names = environment.getPropertySources().stream()
                .filter(EnumerablePropertySource.class::isInstance)
                .map(EnumerablePropertySource.class::cast)
                .flatMap(source -> stream(source.getPropertyNames()))
                .collect(toSet());

        settings.withDevice(new DeviceDefinition().withName("Direct"));

        String prefix = namespace("");
        BeanWrapper wrapper = new BeanWrapperImpl(settings);
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

    public static String toCamelCase(String value) {
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
