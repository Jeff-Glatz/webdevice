package io.webdevice.device;

import io.webdevice.driver.ConfidentialWebDriver;
import org.openqa.selenium.ImmutableCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.net.URL;
import java.util.Objects;
import java.util.Set;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Scope(SCOPE_SINGLETON)
public class RemoteDeviceProvider
        extends BaseDeviceProvider<RemoteWebDriver> {
    private final URL remoteAddress;
    private final Set<String> confidential;

    @Autowired
    public RemoteDeviceProvider(String name, URL remoteAddress, Set<String> confidential) {
        super(name);
        this.remoteAddress = remoteAddress;
        this.confidential = confidential;
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
                name, remoteAddress, capabilities);
        ConfidentialWebDriver driver = new ConfidentialWebDriver(remoteAddress, capabilities, confidential);
        return new Device<>(name, driver, driver.getSessionId());
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
