package io.webdevice.cucumber.split.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * @see io.webdevice.cucumber.split.steps.direct.WebDeviceSteps
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        tags = {"not @ignore"},
        glue = {"io.webdevice.cucumber.split.steps.direct"},
        features = {"src/test/resources/features"},
        plugin = {"pretty"})
public class DirectCucumberIT {
}
