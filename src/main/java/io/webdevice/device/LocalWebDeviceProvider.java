package io.webdevice.device;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.SessionId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import java.util.Objects;

import static java.util.UUID.randomUUID;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Scope(SCOPE_SINGLETON)
public class LocalWebDeviceProvider<Driver extends WebDriver>
        extends BaseWebDeviceProvider<Driver> {
    private final Class<Driver> type;

    @Autowired
    public LocalWebDeviceProvider(String name, Class<Driver> type) {
        super(name);
        this.type = type;
    }

    @Override
    public void initialize() {
        WebDriverManager manager = WebDriverManager.getInstance(type);
        log.info("Setting up {}", type);
        manager.setup();
    }

    @Override
    public WebDevice<Driver> get() {
        log.info("Providing new device named {}", name);
        return new WebDevice<>(name, newDriver(), new SessionId(randomUUID()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LocalWebDeviceProvider<?> that = (LocalWebDeviceProvider<?>) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), type);
    }

    private Driver newDriver() {
        try {
            if (capabilities == null) {
                log.info("Instantiating {} using no-args constructor", type);
                return type.getDeclaredConstructor()
                        .newInstance();
            } else {
                log.info("Instantiating {} with capabilities {}", type, capabilities);
                return type.getDeclaredConstructor(Capabilities.class)
                        .newInstance(capabilities);
            }
        } catch (Exception e) {
            throw new WebDriverException(e);
        }
    }
}
