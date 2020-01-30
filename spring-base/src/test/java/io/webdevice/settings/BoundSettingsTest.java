package io.webdevice.settings;

import org.junit.Before;
import org.springframework.core.env.ConfigurableEnvironment;

public abstract class BoundSettingsTest
        extends EnvironmentBasedTest {
    private SettingsBinder binder;

    @Before
    public void setUpBinder() {
        binder = makeSettingsBinder();
    }

    protected SettingsBinder makeSettingsBinder() {
        return new DefaultSettingsBinder();
    }

    protected Settings bindFrom(ConfigurableEnvironment environment) {
        return binder.from(environment);
    }
}
