package io.webdevice.device;

import io.webdevice.driver.WebDriverDecorator;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;

import java.util.Objects;
import java.util.UUID;

public class LocalWebDevice<Driver extends WebDriver>
        extends WebDriverDecorator<Driver>
        implements WebDevice {
    private final String name;
    private final SessionId sessionId;

    public LocalWebDevice(Driver driver, String name, UUID uuid) {
        super(driver);
        this.name = name;
        this.sessionId = new SessionId(uuid);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SessionId getSessionId() {
        return sessionId;
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
        LocalWebDevice<?> that = (LocalWebDevice<?>) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, sessionId);
    }
}
