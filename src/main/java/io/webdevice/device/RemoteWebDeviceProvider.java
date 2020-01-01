package io.webdevice.device;

import org.openqa.selenium.ImmutableCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.net.URL;
import java.util.Objects;

import static io.webdevice.driver.ConfidentialCapabilities.mask;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Scope(SCOPE_SINGLETON)
public class RemoteWebDeviceProvider
        extends BaseWebDeviceProvider<RemoteWebDeviceProvider> {
    private final URL remoteAddress;

    @Autowired
    public RemoteWebDeviceProvider(String name, URL remoteAddress) {
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
    public RemoteWebDevice get() {
        log.info("Providing new device named {} connecting to {} with capabilities {}",
                name, remoteAddress, mask(capabilities));
        return new RemoteWebDevice(new RemoteWebDriver(remoteAddress, capabilities), name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RemoteWebDeviceProvider that = (RemoteWebDeviceProvider) o;
        return Objects.equals(remoteAddress, that.remoteAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), remoteAddress);
    }
}
