package io.webdevice.wiring;

import io.webdevice.device.DeviceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.Order;

import static io.webdevice.wiring.WebDeviceScope.namespace;
import static java.util.Arrays.asList;
import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

/**
 * This class exists to establish dependencies that ensure proper lifecycle ordering between
 * a {@link io.webdevice.device.WebDevice} instance and all of the {@link DeviceProvider}
 * instances present in the context. {@link DeviceProvider} instances are defined dynamically
 * and not known at compile time, so {@link org.springframework.context.annotation.DependsOn}
 * cannot be used.
 * <p>
 * In all cases, the {@link io.webdevice.device.WebDevice} must be destroyed before all other
 * components so that it will release it's acquired {@link io.webdevice.device.Device} to
 * the originating {@link DeviceProvider}. Then the providers can be destroyed.
 */
@Order(LOWEST_PRECEDENCE)
public class DynamicDependsOn
        implements BeanFactoryPostProcessor {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory factory)
            throws BeansException {
        String webDeviceName = namespace("WebDevice");
        BeanDefinition webDeviceDefinition = factory.getBeanDefinition(webDeviceName);
        String[] providers = factory.getBeanNamesForType(DeviceProvider.class);
        log.info("Establishing {} dependency on {}", webDeviceName, asList(providers));
        webDeviceDefinition.setDependsOn(providers);
    }
}
