package io.webdevice.device;

import io.webdevice.support.ProtectedWebDriver;
import org.openqa.selenium.ImmutableCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.Objects;

import static io.webdevice.device.Devices.remote;

public class RemoteDeviceProvider
        extends BaseDeviceProvider<RemoteWebDriver> {
    private final URL remoteAddress;

    public RemoteDeviceProvider(String name, URL remoteAddress) {
        super(name);
        this.remoteAddress = remoteAddress;
    }

    @Override
    public void initialize() {
        if (capabilities == null) {
            capabilities = new ImmutableCapabilities();
        }
    }

    @Override
    public Device<RemoteWebDriver> get() {
        log.info("Providing new device named {} connecting to {} with capabilities {}",
                name, remoteAddress, maskedCapabilities());
        return remote(name, new ProtectedWebDriver(remoteAddress, capabilities, confidential));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RemoteDeviceProvider that = (RemoteDeviceProvider) o;
        return Objects.equals(remoteAddress, that.remoteAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), remoteAddress);
    }
}
