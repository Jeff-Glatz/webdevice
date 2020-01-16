package io.webdevice.device;

import static java.lang.String.format;

public class DeviceNotProvidedException
        extends RuntimeException {
    private final String device;

    public DeviceNotProvidedException(String device) {
        super(format("The device named %s is not being provided", device));
        this.device = device;
    }

    public DeviceNotProvidedException(String device, Throwable cause) {
        super(format("The device named %s is not being provided", device), cause);
        this.device = device;
    }

    public String getDevice() {
        return device;
    }
}
