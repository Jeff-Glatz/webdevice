package io.webdevice.wiring;

import io.webdevice.support.YamlPropertySourceFactory;
import org.junit.Before;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.env.StandardEnvironment;

public abstract class ConfigurationPropertiesBinderBasedTest
        extends EnvironmentBasedTest {

    @Before
    public void setUpEnvironment() {
        environment = new StandardEnvironment();
        environment.setConversionService(new ApplicationConversionService());
        settingsBinder = new ConfigurationPropertiesBinder();
        propertySourceFactory = new YamlPropertySourceFactory();
    }
}
