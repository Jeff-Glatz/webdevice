package io.webdevice.settings;

import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;

import static io.webdevice.wiring.WebDeviceScope.NAME;

public abstract class EnvironmentTest
        extends UnitTest {
    protected ConfigurableEnvironment environment;
    protected SettingsBinder settingsBinder;
    protected PropertySourceFactory propertySourceFactory;

    @Before
    public void setUpEnvironment() {
        environment = new StandardEnvironment();
        environment.setConversionService(new DefaultConversionService());
        settingsBinder = new DefaultSettingsBinder();
        propertySourceFactory = new DefaultPropertySourceFactory();
    }

    protected ConfigurableEnvironment environmentWith(String... resources)
            throws IOException {
        for (String resource : resources) {
            environment.getPropertySources()
                    .addLast(propertySourceFactory
                            .createPropertySource(NAME,
                                    new EncodedResource(new ClassPathResource(resource))));
        }
        return environment;
    }

    protected Settings settingsFrom(ConfigurableEnvironment environment)
            throws Exception {
        return settingsBinder.from(environment);
    }
}
