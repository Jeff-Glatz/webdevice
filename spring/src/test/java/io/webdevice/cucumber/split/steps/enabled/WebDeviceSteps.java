package io.webdevice.cucumber.split.steps.enabled;

import io.webdevice.cucumber.common.SimpleDeviceSteps;
import io.webdevice.wiring.EnableWebDevice;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This demonstrates using WbeDevice with an existing configuration
 * with the {@link EnableWebDevice} annotation
 */
@SpringBootTest(classes = TestConfiguration.class)
public class WebDeviceSteps
        extends SimpleDeviceSteps {
}
