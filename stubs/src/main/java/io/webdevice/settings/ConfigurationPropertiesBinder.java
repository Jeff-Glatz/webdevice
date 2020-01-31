package io.webdevice.settings;

import org.springframework.context.ApplicationContextException;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.URL;

public class ConfigurationPropertiesBinder
        implements SettingsBinder {

    @Override
    public Settings from(ConfigurableEnvironment environment) {
        try {
            return new Settings()
                    .withBaseUrl(new URL("http://mocked.io"))
                    .withDefaultDevice("Mock Device")
                    .withScope("mock-scope")
                    .withEager(true)
                    .withStrict(false);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new ApplicationContextException("Error creating Settings", e);
        }
    }
}
