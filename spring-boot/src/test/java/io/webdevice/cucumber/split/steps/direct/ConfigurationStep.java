package io.webdevice.cucumber.split.steps.direct;

import io.cucumber.java8.En;
import io.cucumber.spring.CucumberContextConfiguration;
import io.webdevice.wiring.WebDeviceRuntime;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This demonstrates using WebDevice without an existing configuration
 * by directly referencing the {@link WebDeviceRuntime}
 */
@CucumberContextConfiguration
@SpringBootTest(classes = WebDeviceRuntime.class)
public class ConfigurationStep
        implements En {
}
