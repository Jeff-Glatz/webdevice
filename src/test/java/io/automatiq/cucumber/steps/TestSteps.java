package io.automatiq.cucumber.steps;

import io.automatiq.device.Browser;
import io.automatiq.wiring.Automatiq;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Only one step definition can be annotated as the @ContextConfiguration
 * for Spring. This is detected by cucumber-spring library and will serve
 * as the "entry point" for Spring configuration
 */
@SpringBootTest(classes = Automatiq.class)
public class TestSteps {

    @Autowired
    private Browser browser;

    @Given("^a \"(.*)\" browser")
    public void useBrowser(String name) {
        browser.use(name);
    }

    @Given("^I navigate home$")
    public void navigateHome() {
        browser.home();
    }

    @Given("^I navigate to \"(.*)\"$")
    public void navigateTo(String relativePath) {
        browser.navigateTo(relativePath);
    }
}
