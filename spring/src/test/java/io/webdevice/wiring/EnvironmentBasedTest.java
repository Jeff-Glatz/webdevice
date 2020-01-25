package io.webdevice.wiring;

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

public abstract class EnvironmentBasedTest
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

    protected ConfigurableEnvironment environmentWith(String resource)
            throws IOException {
        environment.getPropertySources()
                .addFirst(propertySourceFactory
                        .createPropertySource(NAME,
                                new EncodedResource(new ClassPathResource(resource))));
        return environment;
    }

    protected Settings settings(ConfigurableEnvironment environment)
            throws Exception {
        return settingsBinder.from(environment);
    }
}
