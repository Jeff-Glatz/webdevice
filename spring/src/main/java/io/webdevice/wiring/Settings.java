package io.webdevice.wiring;

import java.io.Serializable;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Collections.unmodifiableMap;
import static org.springframework.util.ClassUtils.isPresent;

public class Settings
        implements Serializable {

    private final Map<String, DeviceDefinition> devices = new LinkedHashMap<>();
    private URL baseUrl;
    private String defaultDevice;
    private boolean eager = false;
    private boolean strict = true;
    private String scope;

    public Map<String, DeviceDefinition> getDevices() {
        return unmodifiableMap(devices);
    }

    public void setDevices(Map<String, DeviceDefinition> devices) {
        this.devices.clear();
        this.devices.putAll(devices);
        this.devices.forEach((name, device) -> device.setName(name));
    }

    public Settings withDevice(DeviceDefinition device) {
        devices.put(device.getName(), device);
        return this;
    }

    public DeviceDefinition device(String name) {
        return devices.get(name);
    }

    public Stream<DeviceDefinition> devices() {
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

    public String getScope() {
        if (scope != null) {
            return scope;
        }
        return defaultScope();
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Settings withScope(String scope) {
        setScope(scope);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Settings settings = (Settings) o;
        return eager == settings.eager &&
                strict == settings.strict &&
                Objects.equals(scope, settings.scope) &&
                Objects.equals(devices, settings.devices) &&
                Objects.equals(baseUrl, settings.baseUrl) &&
                Objects.equals(defaultDevice, settings.defaultDevice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(devices, baseUrl, defaultDevice, eager, strict, scope);
    }

    public static String defaultScope() {
        return isPresent("io.cucumber.spring.CucumberTestContext", null)
                ? "cucumber-glue"
                : "webdevice";
    }
}
