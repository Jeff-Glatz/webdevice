package io.webdevice.cucumber.steps.simple;

import io.cucumber.java.en.Given;
import io.webdevice.device.WebDevice;
import io.webdevice.wiring.WebDeviceRuntime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This demonstrates using WbeDevice without an existing
 * configuration by referencing the {@link WebDeviceRuntime}
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
