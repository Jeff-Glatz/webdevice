package io.webdevice.cucumber.common;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Common runner for all tests that explicitly uses {@link CucumberOptions#extraGlue}
 * to specify where to load common steps, but leaves {@link CucumberOptions#glue}
 * empty so that steps, hooks and features will be loaded from the package of the
 * extending class.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
        tags = {"not @ignore"},
        extraGlue = {"io.webdevice.cucumber.common"},
        plugin = {"pretty"})
public class SimpleRunner {
}
