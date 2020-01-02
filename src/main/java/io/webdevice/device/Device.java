package io.webdevice.device;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Device<Driver extends WebDriver> {
    private final Driver driver;
    private final String name;
    private final Supplier<SessionId> sessionId;
    private final Function<Driver, Boolean> usable;

    public Device(Driver driver, String name, Supplier<SessionId> sessionId, Function<Driver, Boolean> usable) {
        this.driver = driver;
        this.name = name;
        this.sessionId = sessionId;
        this.usable = usable;
    }

    public Driver getDriver() {
        return driver;
    }

    public String getName() {
        return name;
    }

    public SessionId getSessionId() {
        return sessionId.get();
    }

    public boolean usable() {
        return usable.apply(driver);
    }

    public void perform(Consumer<Driver> function) {
        function.accept(driver);
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
