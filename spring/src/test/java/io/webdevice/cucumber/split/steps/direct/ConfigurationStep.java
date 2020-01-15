package io.webdevice.cucumber.split.steps.direct;

import io.cucumber.java8.En;
import io.webdevice.wiring.WebDeviceRuntime;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * This demonstrates using WebDevice without an existing configuration
 * by directly referencing the {@link WebDeviceRuntime}
 */
@DirtiesContext
@SpringBootTest(classes = WebDeviceRuntime.class)
public class ConfigurationStep
        implements En {
}
