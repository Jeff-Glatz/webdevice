package io.webdevice.device;

import io.webdevice.support.ProtectedWebDriver;
import org.openqa.selenium.ImmutableCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

import static io.webdevice.device.Devices.remoteDevice;

public class RemoteDeviceProvider
        extends BaseDeviceProvider<RemoteWebDriver> {
    private final URL remoteAddress;

    public RemoteDeviceProvider(String name, URL remoteAddress) {
        super(name);
        this.remoteAddress = remoteAddress;
    }

    public URL getRemoteAddress() {
        return remoteAddress;
    }

    public void initialize() {
        if (capabilities == null) {
            capabilities = new ImmutableCapabilities();
        }
    }

    @Override
    public Device<RemoteWebDriver> get() {
        log.info("Providing new device named {} connecting to {} with capabilities {}",
                name, remoteAddress, maskedCapabilities());
        return remoteDevice(name, new ProtectedWebDriver(remoteAddress, capabilities, confidential));
    }
}
