package io.webdevice.cucumber.single;

import io.cucumber.java8.En;
import io.webdevice.wiring.EnableWebDevice;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
public class ConfigurationStep
        implements En {
    @Configuration
    @EnableWebDevice
    @PropertySource("classpath:io/webdevice/cucumber/single/webdevice.properties")
    public static class TestConfiguration {
    }
}
