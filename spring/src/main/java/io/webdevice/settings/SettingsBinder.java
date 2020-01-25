package io.webdevice.settings;

import org.springframework.core.env.ConfigurableEnvironment;

public interface SettingsBinder {
    Settings from(ConfigurableEnvironment environment)
            throws Exception;
}
