package io.webdevice.cucumber.lenient;

import io.cucumber.java8.En;
import io.webdevice.support.YamlPropertySourceFactory;
import io.webdevice.support.YamlSupport;
import io.webdevice.wiring.EnableWebDevice;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
public class ConfigurationStep
        implements En {

    @Configuration
    @EnableWebDevice
    @Import(YamlSupport.class)
    @PropertySource(
            value = "classpath:io/webdevice/cucumber/lenient/webdevice.yaml",
            factory = YamlPropertySourceFactory.class)
    public static class TestConfiguration {
    }
}
