package io.webdevice.device;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class DeviceRegistry {
    private final BeanFactory factory;

    @Autowired
    public DeviceRegistry(BeanFactory factory) {
        this.factory = factory;
    }

    @SuppressWarnings("unchecked")
    public <Driver extends WebDriver> Device<Driver> provide(String device) {
        DeviceProvider<Driver> provider = factory.getBean(device, DeviceProvider.class);
        return provider.get();
    }

    @SuppressWarnings("unchecked")
    public <Driver extends WebDriver> void done(Device<Driver> device) {
        DeviceProvider<Driver> provider = factory.getBean(device.getName(), DeviceProvider.class);
        provider.accept(device);
    }
}
