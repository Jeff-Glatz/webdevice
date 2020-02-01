package io.webdevice.device;

import io.webdevice.support.SimpleDeviceCheck;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.function.Supplier;

import static io.webdevice.device.Devices.remoteProvider;

public class StubDevicePool
        extends DevicePool<RemoteWebDriver> {

    public StubDevicePool(String name, Supplier<RemoteWebDriver> supplier) {
        super(name, remoteProvider(name, supplier), new SimpleDeviceCheck<>());
    }
}
