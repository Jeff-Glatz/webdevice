package io.webdevice.cucumber.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * @see io.webdevice.cucumber.steps.auto.WebDeviceSteps
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        tags = {"@local and not @ignore"},
        glue = {"io.webdevice.cucumber.steps.auto"},
        features = {"src/test/resources/features"},
        plugin = {"pretty"})
public class AutoCucumberIT {
}
