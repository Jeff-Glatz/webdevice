package io.webdevice.cucumber.lenient;

import io.cucumber.java8.En;
import io.cucumber.spring.CucumberContextConfiguration;
import io.webdevice.wiring.EnableWebDevice;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
@CucumberContextConfiguration
public class ConfigurationStep
        implements En {

    @Configuration
    @EnableWebDevice(settings = "io/webdevice/cucumber/lenient/webdevice.yaml")
    public static class TestConfiguration {
    }
}
