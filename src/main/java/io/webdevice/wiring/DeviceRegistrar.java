package io.webdevice.wiring;

import io.webdevice.device.DevicePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import static io.webdevice.wiring.Settings.settings;
import static java.lang.String.format;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

@Order
public class DeviceRegistrar
        implements ImportBeanDefinitionRegistrar {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Environment environment;

    @Autowired
    public DeviceRegistrar(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        log.info("Registering devices ...");
        settings(environment).devices()
                .filter(device -> !registry.isBeanNameInUse(device.getName()))
                .forEach(device -> {
                    String provider = maybeDefineProvider(device, registry);
                    if (device.isPooled()) {
                        provider = maybeDefinePool(device, provider, registry);
                    }
                    registerAliases(provider, device, registry);
                });
        log.info("Devices registered.");
    }

    private String maybeDefineProvider(DeviceSettings device, BeanDefinitionRegistry registry) {
        String provider = format("%s-provider", device.getName());
        if (!registry.isBeanNameInUse(provider)) {
            log.info("Registering WebDeviceProvider definition named {}", provider);
            registry.registerBeanDefinition(provider,
                    device.definitionOf()
                            .getBeanDefinition());
        }
        return provider;
    }

    private String maybeDefinePool(DeviceSettings device, String provider, BeanDefinitionRegistry registry) {
        String pool = format("%s-pool", device.getName());
        if (!registry.isBeanNameInUse(pool)) {
            log.info("Registering WebDevicePool definition named {}", pool);
            registry.registerBeanDefinition(pool,
                    genericBeanDefinition(DevicePool.class)
                            .addConstructorArgValue(device.getName())
                            .addConstructorArgReference(provider)
                            .getBeanDefinition());
        }
        return pool;
    }

    private void registerAliases(String canonical, DeviceSettings device, BeanDefinitionRegistry registry) {
        log.info("Registering alias '{}' for '{}'", device.getName(), canonical);
        registry.registerAlias(canonical, device.getName());
        device.aliases().forEach(alias -> {
            log.info("Registering alias '{}' for '{}'", alias, canonical);
            registry.registerAlias(canonical, alias);
        });
    }
}
