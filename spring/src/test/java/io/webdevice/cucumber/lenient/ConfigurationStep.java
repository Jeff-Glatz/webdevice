package io.webdevice.cucumber.lenient;

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
    @PropertySource("classpath:io/webdevice/cucumber/lenient/webdevice.properties")
    public static class TestConfiguration {
    }
}
