package io.webdevice.cucumber.split.steps.enabled;

import io.cucumber.java8.En;
import io.webdevice.wiring.EnableWebDevice;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * This demonstrates using WebDevice with an existing configuration
 * decorated with the {@link EnableWebDevice} annotation
 */
@DirtiesContext
@SpringBootTest(classes = TestConfiguration.class)
public class ConfigurationStep
        implements En {
}
