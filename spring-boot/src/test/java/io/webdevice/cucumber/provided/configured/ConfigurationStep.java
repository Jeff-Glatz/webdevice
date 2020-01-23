package io.webdevice.cucumber.provided.configured;

import io.cucumber.java8.En;
import io.webdevice.support.YamlPropertySourceFactory;
import io.webdevice.wiring.EnableWebDevice;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
public class ConfigurationStep
        implements En {

    @Configuration
    @EnableWebDevice
    @PropertySource(
            value = "classpath:io/webdevice/cucumber/provided/configured/webdevice.yaml",
            factory = YamlPropertySourceFactory.class)
    public static class TestConfiguration {
    }
}
