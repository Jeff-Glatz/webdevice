package io.webdevice.cucumber.split.steps.auto;

import io.cucumber.java8.En;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This demonstrates using WebDevice with an existing
 * configuration leveraging auto configuration
 */
@SpringBootTest(classes = TestConfiguration.class)
public class ConfigurationStep
        implements En {
}
