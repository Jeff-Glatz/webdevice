package io.webdevice.cucumber.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * @see io.webdevice.cucumber.steps.simple.WebDeviceSteps
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        tags = {"not @ignore"},
        glue = {"io.webdevice.cucumber.steps.simple"},
        features = {"src/test/resources/features"},
        plugin = {"pretty"})
public class SimpleCucumberIT {
}
