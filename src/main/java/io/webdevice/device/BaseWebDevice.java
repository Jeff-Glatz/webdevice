package io.webdevice.device;

import org.openqa.selenium.WebDriver;

import java.util.Objects;
import java.util.function.Consumer;

public abstract class BaseWebDevice<Driver extends WebDriver>
        implements WebDevice<Driver> {
    protected final Driver driver;
    protected final String name;

    protected BaseWebDevice(Driver driver, String name) {
        this.driver = driver;
        this.name = name;
    }

    @Override
    public Driver getDriver() {
        return driver;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void perform(Consumer<Driver> function) {
        function.accept(driver);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BaseWebDevice<?> that = (BaseWebDevice<?>) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}
