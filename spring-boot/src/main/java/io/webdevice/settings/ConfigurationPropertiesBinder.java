package io.webdevice.settings;

import io.webdevice.wiring.WebDeviceScope;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * This implementation uses the {@link Binder} to bind {@link Settings} from the
 * execution {@link ConfigurableEnvironment environment}.
 */
public class ConfigurationPropertiesBinder
        implements SettingsBinder {

    @Override
    public Settings from(ConfigurableEnvironment environment) {
        ConfigurationPropertySources.attach(environment);
        return Binder.get(environment)
                .bind(WebDeviceScope.NAME, Settings.class)
                .orElse(new Settings());
    }
}
