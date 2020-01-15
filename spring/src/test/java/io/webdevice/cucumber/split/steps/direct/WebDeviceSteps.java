package io.webdevice.cucumber.split.steps.direct;

import io.webdevice.cucumber.common.SimpleDeviceSteps;
import io.webdevice.wiring.WebDeviceRuntime;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This demonstrates using WbeDevice without an existing configuration
 * by directly referencing the {@link WebDeviceRuntime}
 */
@SpringBootTest(classes = WebDeviceRuntime.class)
public class WebDeviceSteps
        extends SimpleDeviceSteps {
}
