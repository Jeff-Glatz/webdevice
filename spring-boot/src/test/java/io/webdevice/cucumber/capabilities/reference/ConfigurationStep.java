package io.webdevice.cucumber.capabilities.reference;

import io.cucumber.java8.En;
import io.cucumber.spring.CucumberContextConfiguration;
import io.webdevice.wiring.EnableWebDevice;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
@CucumberContextConfiguration
public class ConfigurationStep
        implements En {

    @Configuration
    @EnableWebDevice(settings = "io/webdevice/cucumber/capabilities/reference/webdevice.yaml")
    public static class TestConfiguration {

        @Value("${saucelabs_username}")
        private String username;

        @Value("${saucelabs_accessKey}")
        private String accessKey;

        @Bean
        public FirefoxOptions firefoxOptions() {
            FirefoxOptions options = new FirefoxOptions();
            options.setCapability("username", username);
            options.setCapability("accessKey", accessKey);
            options.setCapability("extendedDebugging", true);
            options.setCapability("platform", "macOS 10.14");
            options.setCapability("version", "latest");
            return options;
        }
    }
}
