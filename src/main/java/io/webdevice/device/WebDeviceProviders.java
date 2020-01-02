package io.webdevice.device;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
public class WebDeviceProviders {
    private final ApplicationContext context;

    @Autowired
    public WebDeviceProviders(ApplicationContext context) {
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    public <Driver extends WebDriver> WebDevice<Driver> provide(String device) {
        WebDeviceProvider<Driver> provider = context.getBean(device, WebDeviceProvider.class);
        return provider.get();
    }

    @SuppressWarnings("unchecked")
    public <Driver extends WebDriver> void done(WebDevice<Driver> device) {
        WebDeviceProvider<Driver> provider = context.getBean(device.getName(), WebDeviceProvider.class);
        provider.accept(device);
    }
}
