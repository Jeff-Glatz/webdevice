package io.webdevice.device;

import io.webdevice.driver.WebDriverDecorator;
import org.openqa.selenium.WebDriver;

import java.util.Objects;

public abstract class BaseWebDevice<Driver extends WebDriver>
        extends WebDriverDecorator<Driver>
        implements WebDevice {
    private final String name;

    protected BaseWebDevice(Driver driver, String name) {
        super(driver);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
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
