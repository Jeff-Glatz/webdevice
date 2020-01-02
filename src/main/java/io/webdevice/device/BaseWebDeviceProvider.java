package io.webdevice.device;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public abstract class BaseWebDeviceProvider<Driver extends WebDriver>
        implements WebDeviceProvider<Driver> {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final String name;

    protected Capabilities capabilities;

    protected BaseWebDeviceProvider(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public Capabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Capabilities capabilities) {
        this.capabilities = capabilities;
    }

    @Override
    public void accept(WebDevice<Driver> device) {
        log.info("Provider {} quitting device {}", name, device.getSessionId());
        device.perform(WebDriver::quit);
    }

    @Override
    public void dispose() {
        log.info("Provider {} shut down.", name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseWebDeviceProvider<Driver> that = (BaseWebDeviceProvider<Driver>) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(capabilities, that.capabilities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, capabilities);
    }
}
