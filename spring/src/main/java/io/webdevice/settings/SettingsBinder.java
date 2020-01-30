package io.webdevice.settings;

import org.springframework.core.env.ConfigurableEnvironment;

/**
 * The {@link SettingsBinder} leverages the Strategy Design Pattern to select an
 * appropriate data binding mechanism to create a {@link Settings} instance from
 * the current execution {@link ConfigurableEnvironment environment}
 */
public interface SettingsBinder {

    /**
     * Supplies the {@link Settings} instance used to configure the
     * {@link io.webdevice.wiring.WebDeviceRuntime}. The {@link Settings}
     * are expected to be derived from supplied execution
     * {@link ConfigurableEnvironment environment}
     *
     * @param environment The current execution
     *         {@link ConfigurableEnvironment environment}
     * @return the {@link Settings} instance used to configure the
     *         {@link io.webdevice.wiring.WebDeviceRuntime}.
     */
    Settings from(ConfigurableEnvironment environment);
}
