package io.webdevice.wiring;

import io.webdevice.device.Device;
import io.webdevice.device.DeviceProvider;
import org.openqa.selenium.WebDriver;

public class SameDeviceProvider<Driver extends WebDriver>
        implements DeviceProvider<Driver> {
    private final Device<Driver> singleton;

    public SameDeviceProvider(Device<Driver> singleton) {
        this.singleton = singleton;
    }

    @Override
    public Device<Driver> get() {
        return singleton;
    }
}
