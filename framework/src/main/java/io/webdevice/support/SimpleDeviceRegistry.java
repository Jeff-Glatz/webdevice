package io.webdevice.support;

import io.webdevice.device.Device;
import io.webdevice.device.DeviceNotProvidedException;
import io.webdevice.device.DeviceProvider;
import io.webdevice.device.DeviceRegistry;
import org.openqa.selenium.WebDriver;

import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleDeviceRegistry
        implements DeviceRegistry {
    private final Map<String, DeviceProvider<? extends WebDriver>> providers =
            new LinkedHashMap<>();

    public <Driver extends WebDriver> SimpleDeviceRegistry withProvider(String device, DeviceProvider<Driver> provider) {
        providers.put(device, provider);
        return this;
    }

    @Override
    public <Driver extends WebDriver> Device<Driver> provide(String device) {
        DeviceProvider<Driver> provider = providerOf(device);
        return provider.get();
    }

    @Override
    public <Driver extends WebDriver> void release(Device<Driver> device) {
        DeviceProvider<Driver> provider = providerOf(device.getName());
        provider.accept(device);
    }

    @SuppressWarnings("unchecked")
    private <Driver extends WebDriver> DeviceProvider<Driver> providerOf(String device) {
        DeviceProvider<Driver> provider = (DeviceProvider<Driver>) providers.get(device);
        if (provider == null) {
            throw new DeviceNotProvidedException(device);
        }
        return provider;
    }
}
