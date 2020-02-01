package io.webdevice.settings;

import java.io.Serializable;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class Settings
        implements Serializable {

    private final Map<String, DeviceDefinition> devices = new DeviceMap();
    private URL baseUrl;
    private String defaultDevice;
    private boolean eager = false;
    private boolean strict = true;
    private String scope;

    public Map<String, DeviceDefinition> getDevices() {
        return devices;
    }

    public void setDevices(Map<String, DeviceDefinition> devices) {
        // In certain data binding scenarios, the getter will be invoked, modified, and then set, so that clearing
        // the local instance actually clears the incoming map (since it is the same instance)
        if (this.devices != devices) {
            this.devices.clear();
            this.devices.putAll(devices);
        }
    }

    public Settings withDevice(DeviceDefinition device) {
        devices.put(device.getName(), device);
        return this;
    }

    public DeviceDefinition device(String name) {
        if (devices.containsKey(name)) {
            return devices.get(name);
        }
        return devices()
                .filter(device -> name.equalsIgnoreCase(device.getName()))
                .findFirst()
                .orElse(null);
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
        return scope;
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

    @Override
    public String toString() {
        return "Settings{" +
                "baseUrl=" + baseUrl +
                ", defaultDevice='" + defaultDevice + '\'' +
                ", eager=" + eager +
                ", strict=" + strict +
                ", scope='" + scope + '\'' +
                ", devices=" + devices +
                '}';
    }

    /**
     * This class exists to ensure key names and device names remain synchronized
     */
    private static class DeviceMap
            extends LinkedHashMap<String, DeviceDefinition> {

        @Override
        public DeviceDefinition put(String key, DeviceDefinition value) {
            return super.put(key, value.withName(key));
        }

        @Override
        public void putAll(Map<? extends String, ? extends DeviceDefinition> map) {
            map.forEach((name, device) -> device.setName(name));
            super.putAll(map);
        }
    }
}
