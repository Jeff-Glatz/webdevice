package io.automatiq.wiring;

import java.io.Serializable;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class Settings
        implements Serializable {
    private URL baseUrl;
    private String defaultDevice;
    private Map<String, Device> devices = new LinkedHashMap<>();

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

    public Map<String, Device> getDevices() {
        return devices;
    }

    public void setDevices(Map<String, Device> devices) {
        this.devices = devices;
        this.devices.forEach((name, device) -> device.setName(name));
    }

    public Settings withDevice(Device device) {
        devices.put(device.getName(), device);
        return this;
    }

    public Stream<Device> devices() {
        return devices.values().stream();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Settings settings = (Settings) o;
        return Objects.equals(baseUrl, settings.baseUrl) &&
                Objects.equals(defaultDevice, settings.defaultDevice) &&
                Objects.equals(devices, settings.devices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseUrl, defaultDevice, devices);
    }
}
