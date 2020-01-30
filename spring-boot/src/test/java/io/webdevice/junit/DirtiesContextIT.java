package io.webdevice.junit;

import io.webdevice.device.WebDevice;
import io.webdevice.wiring.EnableWebDevice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@DirtiesContext
@ContextConfiguration
@RunWith(SpringRunner.class)
public class DirtiesContextIT {

    @Autowired
    private WebDevice browser;

    @Test
    public void shouldUseFirefox() {
        browser.use("LocalFirefox");
        browser.home();
        browser.navigateTo("/tasks");
    }

    @Test
    public void shouldUseChrome() {
        browser.use("LocalChrome");
        browser.home();
        browser.navigateTo("/tasks");
    }

    @Test
    public void shouldUseDefaultDevice() {
        browser.useDefault();
        browser.home();
        browser.navigateTo("/tasks");
    }

    @Configuration
    @EnableWebDevice(settings = "classpath:io/webdevice/junit/prototype-device.yaml")
    public static class Wiring {
    }
}
