package io.webdevice.device;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;

import java.util.Objects;
import java.util.UUID;

public class LocalWebDevice<Driver extends WebDriver>
        extends BaseWebDevice<Driver> {
    private final SessionId sessionId;

    public LocalWebDevice(Driver driver, String name, UUID uuid) {
        super(driver, name);
        this.sessionId = new SessionId(uuid);
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
        return Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sessionId);
    }
}
