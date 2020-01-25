package io.webdevice.wiring;

import io.webdevice.support.YamlPropertySourceFactory;
import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;

import static io.webdevice.wiring.WebDeviceScope.NAME;

public abstract class EnvironmentBasedTest
        extends UnitTest {
    protected ConfigurableEnvironment environment;
    protected SettingsBinder settingsBinder;
    private PropertySourceFactory propertySourceFactory;

    @Before
    public void setUpEnvironment() {
        environment = new StandardEnvironment();
        environment.setConversionService(new ApplicationConversionService());
        settingsBinder = new ConfigurationPropertiesBinder();
        propertySourceFactory = new YamlPropertySourceFactory();
    }

    protected ConfigurableEnvironment environmentWith(String resource)
            throws IOException {
        environment.getPropertySources()
                .addFirst(propertySourceFactory
                        .createPropertySource(NAME,
                                new EncodedResource(new ClassPathResource(resource))));
        return environment;
    }

    protected Settings settings(ConfigurableEnvironment environment) {
        return settingsBinder.from(environment);
    }
}
