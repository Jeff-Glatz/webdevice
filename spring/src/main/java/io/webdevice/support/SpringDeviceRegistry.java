package io.webdevice.support;

import io.webdevice.device.Device;
import io.webdevice.device.DeviceNotProvidedException;
import io.webdevice.device.DeviceProvider;
import io.webdevice.device.DeviceRegistry;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class SpringDeviceRegistry
        implements DeviceRegistry {
    private final BeanFactory factory;

    @Autowired
    public SpringDeviceRegistry(BeanFactory factory) {
        this.factory = factory;
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
        try {
            return factory.getBean(device, DeviceProvider.class);
        } catch (BeansException e) {
            throw new DeviceNotProvidedException(device, e);
        }
    }
}
