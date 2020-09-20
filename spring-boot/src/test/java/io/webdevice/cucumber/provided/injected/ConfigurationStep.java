package io.webdevice.cucumber.provided.injected;

import io.cucumber.java8.En;
import io.cucumber.spring.CucumberContextConfiguration;
import io.webdevice.device.DeviceProvider;
import io.webdevice.support.CustomFirefoxProvider;
import io.webdevice.wiring.EnableWebDevice;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
@CucumberContextConfiguration
public class ConfigurationStep
        implements En {

    @Configuration
    @EnableWebDevice(settings = "io/webdevice/cucumber/provided/injected/webdevice.yaml")
    public static class TestConfiguration {

        @Bean("Provided")
        public DeviceProvider<FirefoxDriver> provided() {
            return new CustomFirefoxProvider("Provided");
        }
    }
}
