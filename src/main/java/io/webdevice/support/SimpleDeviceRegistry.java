package io.webdevice.support;

import io.webdevice.device.Device;
import io.webdevice.device.DeviceProvider;
import io.webdevice.device.DeviceRegistry;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.webdevice.device.DeviceProvider.providing;

public class SimpleDeviceRegistry
        implements DeviceRegistry {
    private final Map<String, DeviceProvider<? extends WebDriver>> providers =
            new LinkedHashMap<>();

    public <Driver extends WebDriver> SimpleDeviceRegistry withProvider(String device, DeviceProvider<Driver> provider) {
        providers.put(device, provider);
        return this;
    }

    public <Driver extends WebDriver> SimpleDeviceRegistry withProvider(String device,
                                                                        Supplier<Driver> supplier,
                                                                        Function<Driver, SessionId> session,
                                                                        Function<Driver, Boolean> usable) {
        return withProvider(device, providing(device, supplier, session, usable));
    }

    public <Driver extends RemoteWebDriver> SimpleDeviceRegistry withProvider(String device,
                                                                              Supplier<Driver> supplier,
                                                                              Function<Driver, Boolean> usable) {
        return withProvider(device, providing(device, supplier, usable));
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
