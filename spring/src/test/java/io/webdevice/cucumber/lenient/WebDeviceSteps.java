package io.webdevice.cucumber.lenient;

import io.cucumber.java.en.Given;
import io.webdevice.device.WebDevice;
import io.webdevice.wiring.EnableWebDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration
public class WebDeviceSteps {

    @Autowired
    private WebDevice browser;

    @Given("I navigate home")
    public void navigateHome() {
        browser.home();
    }

    @Given("I navigate to {string}")
    public void navigateTo(String relativePath) {
        browser.navigateTo(relativePath);
    }

    @Given("a {string} browser")
    public void useBrowser(String name) {
        browser.use(name);
    }

    @Configuration
    @EnableWebDevice
    @PropertySource("classpath:io/webdevice/cucumber/lenient/webdevice.properties")
    public static class TestConfiguration {
    }
}
