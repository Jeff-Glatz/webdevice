package io.webdevice.scenario.spring.base;

import io.webdevice.device.WebDevice;
import io.webdevice.wiring.EnableWebDevice;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@ContextConfiguration
@RunWith(SpringRunner.class)
public class WebDeviceScopeIT {

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
    @EnableWebDevice(settings = "devices/local-devices.yaml", scope = "webdevice")
    public static class Wiring {
    }
}
