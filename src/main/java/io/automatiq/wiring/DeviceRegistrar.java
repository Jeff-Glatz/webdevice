package io.automatiq.wiring;

import io.automatiq.device.WebDevicePool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import static io.automatiq.wiring.Automatiq.PREFIX;
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
        Settings settings = Binder.get(environment)
                .bind(PREFIX, Settings.class)
                .get();

        log.info("Registering devices ...");
        settings.devices()
                .filter(device -> !registry.isBeanNameInUse(device.getName()))
                .forEach(device -> maybeDefinePool(device, registry,
                        maybeDefineProvider(device, registry)));
        log.info("Devices registered.");
    }

    private String maybeDefineProvider(Device device, BeanDefinitionRegistry registry) {
        String provider = format("%s-provider", device.getName());
        if (!registry.isBeanNameInUse(provider)) {
            log.info("Registering WebDeviceProvider definition named {}", provider);
            registry.registerBeanDefinition(provider,
                    device.definitionOf()
                            .getBeanDefinition());
        }
        return provider;
    }

    private void maybeDefinePool(Device device, BeanDefinitionRegistry registry, String provider) {
        String pool = format("%s-pool", device.getName());
        if (!registry.isBeanNameInUse(pool)) {
            log.info("Registering WebDevicePool definition named {}", pool);
            registry.registerBeanDefinition(pool,
                    genericBeanDefinition(WebDevicePool.class)
                            .addConstructorArgReference(provider)
                            .getBeanDefinition());
            log.info("Registering alias {} for {}", device.getName(), pool);
            registry.registerAlias(pool, device.getName());
        }
    }
}
