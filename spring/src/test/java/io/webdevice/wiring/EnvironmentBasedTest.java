package io.webdevice.wiring;

import io.webdevice.support.YamlPropertySourceFactory;
import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;

import static io.webdevice.wiring.Settings.PREFIX;

public abstract class EnvironmentBasedTest
        extends UnitTest {
    protected StandardEnvironment environment;
    private PropertySourceFactory propertySourceFactory;

    @Before
    public void setUpEnvironment() {
        environment = new StandardEnvironment();
        environment.setConversionService(new ApplicationConversionService());
        propertySourceFactory = new YamlPropertySourceFactory();
    }

    protected StandardEnvironment environmentWith(String resource)
            throws IOException {
        environment.getPropertySources()
                .addFirst(propertySourceFactory
                        .createPropertySource(PREFIX,
                                new EncodedResource(new ClassPathResource(resource))));
        return environment;
    }
}
