package io.webdevice.support;

import io.webdevice.device.BaseDeviceProvider;
import io.webdevice.device.Device;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import static io.webdevice.device.Devices.directDevice;

public class GenericDeviceProvider
        extends BaseDeviceProvider<RemoteWebDriver> {

    public GenericDeviceProvider(String name) {
        super(name);
    }

    @Override
    public Device<RemoteWebDriver> get() {
        return directDevice(name, new RemoteWebDriver(new MutableCapabilities()));
    }
}
