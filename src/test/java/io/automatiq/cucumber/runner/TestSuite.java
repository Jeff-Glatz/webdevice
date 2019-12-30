package io.automatiq.cucumber.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        glue = {"io.automatiq.cucumber.steps"},
        features = {"src/test/resources/features"},
        plugin = {"pretty"})
public class TestSuite {
}
