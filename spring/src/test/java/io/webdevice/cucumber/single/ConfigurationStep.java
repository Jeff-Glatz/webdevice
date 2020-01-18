package io.webdevice.cucumber.single;

import io.cucumber.java8.En;
import io.webdevice.wiring.EnableWebDevice;
import io.webdevice.wiring.EnableYamlBinding;
import io.webdevice.wiring.YamlPropertySourceFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
public class ConfigurationStep
        implements En {

    @Configuration
    @EnableWebDevice
    @EnableYamlBinding
    @PropertySource(
            value = "classpath:io/webdevice/cucumber/single/webdevice.yaml",
            factory = YamlPropertySourceFactory.class)
    public static class TestConfiguration {
    }
}
