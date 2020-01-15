package io.webdevice.cucumber.provided.common;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.webdevice.device.BaseDeviceProvider;
import io.webdevice.device.Device;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import static io.webdevice.device.Devices.directDevice;

public class CustomDeviceProvider
        extends BaseDeviceProvider<FirefoxDriver> {

    @Autowired
    public CustomDeviceProvider(String name) {
        super(name);
    }

    @PostConstruct
    public void initialize() {
        WebDriverManager manager = WebDriverManager.firefoxdriver();
        manager.setup();
    }

    @Override
    public Device<FirefoxDriver> get() {
        return directDevice(name, new FirefoxDriver());
    }
}
