package io.webdevice.device;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import static io.webdevice.device.Devices.directDevice;

public class StubDeviceProvider
        extends BaseDeviceProvider<RemoteWebDriver> {

    public StubDeviceProvider(String name) {
        super(name);
    }

    @Override
    public Device<RemoteWebDriver> get() {
        return directDevice(name, new RemoteWebDriver(new MutableCapabilities()));
    }
}
