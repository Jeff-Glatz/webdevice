package io.webdevice.cucumber.capabilities.reference;

import io.cucumber.java8.En;
import io.webdevice.support.YamlPropertySourceFactory;
import io.webdevice.wiring.EnableWebDevice;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
public class ConfigurationStep
        implements En {

    @Configuration
    @EnableWebDevice
    @PropertySource(
            value = "classpath:io/webdevice/cucumber/capabilities/reference/webdevice.yaml",
            factory = YamlPropertySourceFactory.class)
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
