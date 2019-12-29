package automaton.cucumber.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        glue = {"automaton.cucumber.steps"},
        features = {"src/test/resources/features"},
        plugin = {"pretty"})
public class TestSuite {
}
