package io.webdevice.wiring;

import java.io.Serializable;
import java.net.URL;
import java.util.Objects;

public class BrowserSettings
        implements Serializable {
    private URL baseUrl;
    private String defaultDevice;
    private boolean eager = false;
    private boolean strict = true;

    public URL getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    public BrowserSettings withBaseUrl(URL baseUrl) {
        setBaseUrl(baseUrl);
        return this;
    }

    public String getDefaultDevice() {
        return defaultDevice;
    }

    public void setDefaultDevice(String defaultDevice) {
        this.defaultDevice = defaultDevice;
    }

    public BrowserSettings withDefaultDevice(String defaultDevice) {
        setDefaultDevice(defaultDevice);
        return this;
    }

    public boolean isEager() {
        return eager;
    }

    public void setEager(boolean eager) {
        this.eager = eager;
    }

    public BrowserSettings withEager(boolean eager) {
        setEager(eager);
        return this;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public BrowserSettings withStrict(boolean strict) {
        setStrict(strict);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BrowserSettings that = (BrowserSettings) o;
        return eager == that.eager &&
                strict == that.strict &&
                Objects.equals(baseUrl, that.baseUrl) &&
                Objects.equals(defaultDevice, that.defaultDevice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(baseUrl, defaultDevice, eager, strict);
    }
}
