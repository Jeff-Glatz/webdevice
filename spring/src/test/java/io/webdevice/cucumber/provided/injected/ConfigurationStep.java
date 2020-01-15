package io.webdevice.cucumber.provided.injected;

import io.cucumber.java8.En;
import io.webdevice.cucumber.provided.common.CustomDeviceProvider;
import io.webdevice.device.DeviceProvider;
import io.webdevice.wiring.EnableWebDevice;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
public class ConfigurationStep
        implements En {
    @Configuration
    @EnableWebDevice
    @PropertySource("classpath:io/webdevice/cucumber/provided/injected/webdevice.properties")
    public static class TestConfiguration {
        @Bean("Provided")
        public DeviceProvider<FirefoxDriver> provided() {
            return new CustomDeviceProvider("Provided");
        }
    }
}
