package io.webdevice.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Component
@Scope(SCOPE_SINGLETON)
public class WebDeviceProviders {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final ApplicationContext context;

    @Autowired
    public WebDeviceProviders(ApplicationContext context) {
        this.context = context;
    }

    /**
     * Returns the provider for the named device.
     *
     * @param device   The name of the device
     * @param <Device> The expected concrete device
     * @return The named {@link WebDeviceProvider}
     */
    @SuppressWarnings("unchecked")
    public <Device extends WebDevice> WebDeviceProvider<Device> providerOf(String device) {
        return context.getBean(device, WebDeviceProvider.class);
    }

    /**
     * Returns the provider for the device.
     *
     * @param device   The device
     * @param <Device> The expected concrete device
     * @return The named {@link WebDeviceProvider}
     */
    @SuppressWarnings("unchecked")
    public <Device extends WebDevice> WebDeviceProvider<Device> providerOf(WebDevice device) {
        return providerOf(device.getName());
    }
}
