package io.webdevice.settings;

import org.springframework.core.env.ConfigurableEnvironment;

/**
 * The {@link SettingsBinder} leverages the Strategy Design Pattern to select an
 * appropriate data binding mechanism to create a {@link Settings} instance from
 * the current execution {@link ConfigurableEnvironment environment}
 */
public interface SettingsBinder {
    Settings from(ConfigurableEnvironment environment)
            throws Exception;
}
