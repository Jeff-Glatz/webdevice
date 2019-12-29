package automaton.device;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import java.util.Objects;

import static java.util.UUID.randomUUID;

public class LocalWebDeviceProvider<Driver extends WebDriver>
        extends BaseWebDeviceProvider<LocalWebDevice<Driver>> {
    private final Class<Driver> type;

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
    public LocalWebDevice<Driver> get() {
        return new LocalWebDevice<>(newDriver(), name, randomUUID());
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
                log.info("Instantiating {} using capabilities constructor: {}", type, capabilities);
                return type.getDeclaredConstructor(Capabilities.class)
                        .newInstance(capabilities);
            }
        } catch (Exception e) {
            throw new WebDriverException(e);
        }
    }
}
