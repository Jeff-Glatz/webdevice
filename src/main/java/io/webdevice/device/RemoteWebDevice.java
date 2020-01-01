package io.webdevice.device;

import io.webdevice.driver.WebDriverDecorator;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import java.util.Objects;

public class RemoteWebDevice
        extends WebDriverDecorator<RemoteWebDriver>
        implements WebDevice {
    private final String name;

    public RemoteWebDevice(RemoteWebDriver driver, String name) {
        super(driver);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SessionId getSessionId() {
        return delegate.getSessionId();
    }

    @Override
    public boolean usable() {
        // TODO: How to determine liveness?
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RemoteWebDevice that = (RemoteWebDevice) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}
