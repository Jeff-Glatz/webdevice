package io.webdevice.wiring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static io.webdevice.wiring.Settings.PREFIX;
import static java.util.Collections.unmodifiableMap;

@ConfigurationProperties(PREFIX)
public class Settings
        implements Serializable {
    public static final String PREFIX = "webdevice";

    private final Map<String, DeviceSettings> devices = new LinkedHashMap<>();
    private BrowserSettings browser;

    public static Settings settings(Environment environment) {
        return Binder.get(environment)
                .bind(PREFIX, Settings.class)
                .get();
    }

    public Map<String, DeviceSettings> getDevices() {
        return unmodifiableMap(devices);
    }

    public void setDevices(Map<String, DeviceSettings> devices) {
        this.devices.clear();
        this.devices.putAll(devices);
        this.devices.forEach((name, device) -> device.setName(name));
    }

    public Settings withDevice(DeviceSettings device) {
        devices.put(device.getName(), device);
        return this;
    }

    public Stream<DeviceSettings> devices() {
        return devices.values().stream();
    }

    public BrowserSettings getBrowser() {
        return browser;
    }

    public void setBrowser(BrowserSettings browser) {
        this.browser = browser;
    }

    public Settings withBrowser(BrowserSettings browser) {
        setBrowser(browser);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Settings settings = (Settings) o;
        return Objects.equals(devices, settings.devices) &&
                Objects.equals(browser, settings.browser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(devices, browser);
    }
}
