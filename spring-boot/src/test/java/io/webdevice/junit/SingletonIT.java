package io.webdevice.junit;

import io.webdevice.device.WebDevice;
import io.webdevice.wiring.EnableWebDevice;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@ContextConfiguration
@RunWith(SpringRunner.class)
public class SingletonIT {

    @Autowired
    private WebDevice browser;

    @After
    public void releaseBrowser() {
        browser.release();
    }

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
    @EnableWebDevice(settings = "io/webdevice/junit/singleton-device.yaml")
    public static class Wiring {
    }
}
