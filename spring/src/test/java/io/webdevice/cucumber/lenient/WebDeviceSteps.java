package io.webdevice.cucumber.lenient;

import io.webdevice.cucumber.common.SimpleDeviceSteps;
import io.webdevice.wiring.EnableWebDevice;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
public class WebDeviceSteps
        extends SimpleDeviceSteps {

    @Configuration
    @EnableWebDevice
    @PropertySource("classpath:io/webdevice/cucumber/lenient/webdevice.properties")
    public static class TestConfiguration {
    }
}
