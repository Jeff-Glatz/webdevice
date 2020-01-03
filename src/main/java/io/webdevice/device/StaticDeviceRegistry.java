package io.webdevice.device;

import org.openqa.selenium.WebDriver;

import java.util.LinkedHashMap;
import java.util.Map;

public class StaticDeviceRegistry
        implements DeviceRegistry {
    private final Map<String, DeviceProvider<? extends WebDriver>> providers =
            new LinkedHashMap<>();

    public <Driver extends WebDriver> StaticDeviceRegistry withProvider(String device, DeviceProvider<Driver> provider) {
        providers.put(device, provider);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Driver extends WebDriver> Device<Driver> provide(String device) {
        DeviceProvider<Driver> provider = (DeviceProvider<Driver>) providers.get(device);
        return provider.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <Driver extends WebDriver> void release(Device<Driver> device) {
        DeviceProvider<Driver> provider = (DeviceProvider<Driver>) providers.get(device.getName());
        provider.accept(device);
    }
}
