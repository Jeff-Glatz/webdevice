package io.webdevice.wiring;

import org.springframework.core.env.ConfigurableEnvironment;

public interface SettingsBinder {
    Settings from(ConfigurableEnvironment environment)
            throws Exception;
}
