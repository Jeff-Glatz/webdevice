package io.webdevice.device;

import org.openqa.selenium.WebDriver;

public interface DeviceRegistry {
    <Driver extends WebDriver> Device<Driver> provide(String device);
    <Driver extends WebDriver> void release(Device<Driver> device);
}
