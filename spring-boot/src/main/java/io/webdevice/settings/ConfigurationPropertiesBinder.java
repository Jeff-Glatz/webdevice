package io.webdevice.settings;

import io.webdevice.wiring.WebDeviceScope;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;

public class ConfigurationPropertiesBinder
        implements SettingsBinder {

    @Override
    public Settings from(ConfigurableEnvironment environment) {
        return Binder.get(environment)
                .bind(WebDeviceScope.NAME, Settings.class)
                .orElse(new Settings());
    }
}
