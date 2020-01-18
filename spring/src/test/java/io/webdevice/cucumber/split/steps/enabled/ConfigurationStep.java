package io.webdevice.cucumber.split.steps.enabled;

import io.cucumber.java8.En;
import io.webdevice.wiring.EnableWebDevice;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This demonstrates using WebDevice with an existing configuration
 * decorated with the {@link EnableWebDevice} annotation
 */
@SpringBootTest(classes = TestConfiguration.class)
public class ConfigurationStep
        implements En {
}
