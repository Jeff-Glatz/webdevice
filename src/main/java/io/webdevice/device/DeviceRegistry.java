package io.webdevice.device;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
public class DeviceRegistry {
    private final ApplicationContext context;

    @Autowired
    public DeviceRegistry(ApplicationContext context) {
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    public <Driver extends WebDriver> Device<Driver> provide(String device) {
        DeviceProvider<Driver> provider = context.getBean(device, DeviceProvider.class);
        return provider.get();
    }

    @SuppressWarnings("unchecked")
    public <Driver extends WebDriver> void done(Device<Driver> device) {
        DeviceProvider<Driver> provider = context.getBean(device.getName(), DeviceProvider.class);
        provider.accept(device);
    }
}
