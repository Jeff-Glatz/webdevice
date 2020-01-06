package io.webdevice.cucumber.steps;

import io.cucumber.java.en.Given;
import io.webdevice.device.WebDevice;
import io.webdevice.wiring.WebDeviceRuntime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Only one step definition can be annotated as the @ContextConfiguration
 * for Spring. This is detected by cucumber-spring library and will serve
 * as the "entry point" for Spring configuration
 */
@SpringBootTest(classes = WebDeviceRuntime.class)
public class WebDeviceSteps {

    @Autowired
    private WebDevice browser;

    @Given("a {string} browser")
    public void useBrowser(String name) {
        browser.use(name);
    }

    @Given("a browser")
    public void useBrowser() {
        browser.useDefault();
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
