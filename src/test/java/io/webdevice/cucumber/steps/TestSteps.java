package io.webdevice.cucumber.steps;

import io.webdevice.device.Browser;
import io.webdevice.wiring.WebDeviceWiring;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Only one step definition can be annotated as the @ContextConfiguration
 * for Spring. This is detected by cucumber-spring library and will serve
 * as the "entry point" for Spring configuration
 */
@SpringBootTest(classes = WebDeviceWiring.class)
public class TestSteps {

    @Autowired
    private Browser browser;

    @Given("a {string} browser")
    public void useBrowser(String name) {
        browser.use(name);
    }

    @Given("I navigate home")
    public void navigateHome() {
        browser.home();
    }

    @Given("I navigate to {string}")
    public void navigateTo(String relativePath) {
        browser.navigateTo(relativePath);
    }
}
