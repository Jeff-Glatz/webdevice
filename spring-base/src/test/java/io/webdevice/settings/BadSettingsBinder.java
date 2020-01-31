package io.webdevice.settings;

import org.springframework.core.env.ConfigurableEnvironment;

public class BadSettingsBinder
        implements SettingsBinder {

    public BadSettingsBinder() {
        throw new IllegalStateException("not-good");
    }

    @Override
    public Settings from(ConfigurableEnvironment environment) {
        return null;
    }
}
