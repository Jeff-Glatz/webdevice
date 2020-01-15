package io.webdevice.cucumber.split.steps.auto;

import io.webdevice.cucumber.common.SimpleDeviceSteps;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This demonstrates using WbeDevice with an existing
 * configuration with auto configuration enabled
 */
@SpringBootTest(classes = TestConfiguration.class)
public class WebDeviceSteps
        extends SimpleDeviceSteps {
}
