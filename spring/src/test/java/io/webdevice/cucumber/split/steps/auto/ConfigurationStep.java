package io.webdevice.cucumber.split.steps.auto;

import io.cucumber.java8.En;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

/**
 * This demonstrates using WebDevice with an existing
 * configuration leveraging auto configuration
 */
@DirtiesContext
@SpringBootTest(classes = TestConfiguration.class)
public class ConfigurationStep
        implements En {
}
