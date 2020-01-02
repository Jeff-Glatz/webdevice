package io.webdevice.wiring;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;

import java.io.Serializable;
import java.net.URL;
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
    private URL baseUrl;
    private String defaultDevice;
    private boolean eager = false;
    private boolean strict = true;

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

    public URL getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Settings withBaseUrl(URL baseUrl) {
        setBaseUrl(baseUrl);
        return this;
    }

    public String getDefaultDevice() {
        return defaultDevice;
    }

    public void setDefaultDevice(String defaultDevice) {
        this.defaultDevice = defaultDevice;
    }

    public Settings withDefaultDevice(String defaultDevice) {
        setDefaultDevice(defaultDevice);
        return this;
    }

    public boolean isEager() {
        return eager;
    }

    public void setEager(boolean eager) {
        this.eager = eager;
    }

    public Settings withEager(boolean eager) {
        setEager(eager);
        return this;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public Settings withStrict(boolean strict) {
        setStrict(strict);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Settings settings = (Settings) o;
        return eager == settings.eager &&
                strict == settings.strict &&
                Objects.equals(devices, settings.devices) &&
                Objects.equals(baseUrl, settings.baseUrl) &&
                Objects.equals(defaultDevice, settings.defaultDevice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(devices, baseUrl, defaultDevice, eager, strict);
    }
}
