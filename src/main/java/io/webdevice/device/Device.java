package io.webdevice.device;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;

import java.util.Objects;
import java.util.function.Consumer;

public class Device<Driver extends WebDriver> {
    private final String name;
    private final Driver driver;
    private final SessionId sessionId;

    public Device(String name, Driver driver, SessionId sessionId) {
        this.name = name;
        this.driver = driver;
        this.sessionId = sessionId;
    }

    public String getName() {
        return name;
    }

    public Driver getDriver() {
        return driver;
    }

    public SessionId getSessionId() {
        return sessionId;
    }

    public void perform(Consumer<Driver> function) {
        function.accept(driver);
    }

    public boolean usable() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device<?> that = (Device<?>) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(driver, that.driver) &&
                Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, driver, sessionId);
    }
}
