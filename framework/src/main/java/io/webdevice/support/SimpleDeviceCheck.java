package io.webdevice.support;

import io.webdevice.device.Device;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Predicate;

import static java.lang.String.format;

public class SimpleDeviceCheck<Driver extends WebDriver>
        implements Predicate<Device<Driver>> {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean test(Device<Driver> device) {
        Driver driver = device.getDriver();
        try {
            driver.getCurrentUrl();
            return true;
        } catch (WebDriverException e) {
            log.warn(format("Device %s failed validity check", device.getSessionId()), e);
            return false;
        }
    }
}
