package io.webdevice.cucumber.common;

import io.cucumber.java.en.Given;
import io.webdevice.device.WebDevice;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SimpleDeviceSteps {

    @Autowired
    protected WebDevice browser;

    @Given("the default browser is used")
    public void useBrowser() {
        browser.useDefault();
    }

    @Given("a {string} browser is used")
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
