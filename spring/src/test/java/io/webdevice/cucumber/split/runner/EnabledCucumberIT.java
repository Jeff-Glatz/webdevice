package io.webdevice.cucumber.split.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * @see io.webdevice.cucumber.split.steps.enabled.WebDeviceSteps
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        tags = {"@direct and not @ignore"},
        glue = {"io.webdevice.cucumber.split.steps.enabled"},
        features = {"src/test/resources/features"},
        plugin = {"pretty"})
public class EnabledCucumberIT {
}
