package io.webdevice.settings;

import org.junit.Before;
import org.springframework.core.env.ConfigurableEnvironment;

public abstract class SettingsBasedTest
        extends EnvironmentBasedTest {
    protected SettingsBinder settingsBinder;

    @Before
    public void setUpBinder() {
        settingsBinder = makeSettingsBinder();
    }

    protected SettingsBinder makeSettingsBinder() {
        return new BeanWrapperBinder();
    }

    protected Settings settingsFrom(ConfigurableEnvironment environment)
            throws Exception {
        return settingsBinder.from(environment);
    }
}
