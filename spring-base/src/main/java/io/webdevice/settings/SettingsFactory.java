package io.webdevice.settings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.env.ConfigurableEnvironment;

import static io.webdevice.wiring.WebDeviceScope.namespace;
import static java.lang.String.format;
import static org.springframework.util.ClassUtils.forName;
import static org.springframework.util.ClassUtils.isPresent;

/**
 * The {@link SettingsFactory} is a {@link SettingsBinder} implementation
 * that discovers the appropriate {@link SettingsBinder} implementation to
 * use based on the {@code webdevice.binder} environment property.
 *
 * @see SettingsBinder
 * @see DefaultSettingsBinder
 */
public class SettingsFactory
        implements SettingsBinder {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ConfigurableEnvironment environment;

    public SettingsFactory(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public Settings from(ConfigurableEnvironment environment) {
        SettingsBinder binder = binder();
        return binder.from(environment);
    }

    private String discoverImplementation() {
        // First look for an environment property
        String impl = environment.getProperty(
                namespace("binder"), String.class, SettingsBinder.class.getName());
        // If a custom binder was not specified in the environment,
        // then see if configuration properties are available
        if (SettingsBinder.class.getName().equals(impl)) {
            impl = springBootBinderAvailable()
                    ? "io.webdevice.settings.ConfigurationPropertiesBinder"
                    : DefaultSettingsBinder.class.getName();
        }
        return impl;
    }

    private SettingsBinder binder() {
        String impl = discoverImplementation();
        log.info("Using {} to bind Settings from environment", impl);
        try {
            return ((Class<? extends SettingsBinder>) forName(impl, null))
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationContextException(format("Failure instantiating %s", impl), e);
        }
    }

    private static boolean springBootBinderAvailable() {
        return isPresent("org.springframework.boot.context.properties.bind.Binder", null) &&
                isPresent("io.webdevice.settings.ConfigurationPropertiesBinder", null);
    }
}
