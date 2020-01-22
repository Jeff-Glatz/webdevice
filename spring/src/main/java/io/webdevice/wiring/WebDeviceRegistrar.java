package io.webdevice.wiring;

import io.webdevice.device.DevicePool;
import io.webdevice.device.WebDevice;
import io.webdevice.support.SimpleDeviceCheck;
import io.webdevice.support.SpringDeviceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import static java.lang.String.format;
import static org.springframework.beans.factory.support.AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

@Order
public class WebDeviceRegistrar
        implements ImportBeanDefinitionRegistrar {
    public static final String SETTINGS_PREFIX = "webdevice";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Environment environment;

    @Autowired
    public WebDeviceRegistrar(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        Settings settings = settings(environment);
        registerSettings(settings, registry);
        registerDevices(settings, registry);
        registerDeviceRegistry(settings, registry);
        registerWebDevice(settings, registry);
    }

    private String maybeRegisterProvider(DeviceDefinition device, BeanDefinitionRegistry registry) {
        String provider = name(format("%s-Provider", device.getName()));
        if (!registry.isBeanNameInUse(provider)) {
            log.info("Registering WebDeviceProvider definition named {}", provider);
            registry.registerBeanDefinition(provider,
                    device.build()
                            .getBeanDefinition());
        }
        return provider;
    }

    private String maybeRegisterPool(String provider, DeviceDefinition device, BeanDefinitionRegistry registry) {
        String pool = name(format("%s-Pool", device.getName()));
        if (!registry.isBeanNameInUse(pool)) {
            log.info("Registering WebDevicePool definition named {}", pool);
            registry.registerBeanDefinition(pool,
                    genericBeanDefinition(DevicePool.class)
                            .addConstructorArgValue(device.getName())
                            .addConstructorArgReference(provider)
                            .addConstructorArgValue(new SimpleDeviceCheck<>())
                            .setAutowireMode(AUTOWIRE_CONSTRUCTOR)
                            .setDestroyMethodName("dispose")
                            .getBeanDefinition());
        }
        return pool;
    }

    private void registerAliases(String canonical, DeviceDefinition device, BeanDefinitionRegistry registry) {
        log.info("Registering alias '{}' for '{}'", device.getName(), canonical);
        registry.registerAlias(canonical, device.getName());
        device.aliases().forEach(alias -> {
            log.info("Registering alias '{}' for '{}'", alias, canonical);
            registry.registerAlias(canonical, alias);
        });
    }

    private void registerSettings(Settings settings, BeanDefinitionRegistry registry) {
        log.info("Registering Settings ...");
        registry.registerBeanDefinition(name("Settings"),
                genericBeanDefinition(Settings.class, () -> settings)
                        .getBeanDefinition());
        log.info("Settings registered.");
    }

    private void registerDevices(Settings settings, BeanDefinitionRegistry registry) {
        log.info("Registering Devices ...");
        settings.devices()
                .filter(device -> {
                    boolean defined = registry.isBeanNameInUse(name(device.getName()));
                    if (defined) {
                        log.warn("Device {} is already defined, skipping registration", device.getName());
                    }
                    return !defined;
                })
                .forEach(device -> {
                    String provider = maybeRegisterProvider(device, registry);
                    if (device.isPooled()) {
                        provider = maybeRegisterPool(provider, device, registry);
                    }
                    registerAliases(provider, device, registry);
                });
        log.info("Devices registered.");
    }

    private void registerDeviceRegistry(Settings settings, BeanDefinitionRegistry registry) {
        log.info("Registering DeviceRegistry ...");
        registry.registerBeanDefinition(name("DeviceRegistry"),
                genericBeanDefinition(SpringDeviceRegistry.class)
                        .setScope(settings.getScope())
                        .setAutowireMode(AUTOWIRE_CONSTRUCTOR)
                        .getBeanDefinition());
        log.info("DeviceRegistry registered.");
    }

    private void registerWebDevice(Settings settings, BeanDefinitionRegistry registry) {
        log.info("Registering WebDevice ...");
        registry.registerBeanDefinition(name("WebDevice"),
                genericBeanDefinition(WebDevice.class)
                        .setScope(settings.getScope())
                        .addConstructorArgReference(name("DeviceRegistry"))
                        .setAutowireMode(AUTOWIRE_CONSTRUCTOR)
                        .addPropertyValue("baseUrl", settings.getBaseUrl())
                        .addPropertyValue("defaultDevice", settings.getDefaultDevice())
                        .addPropertyValue("eager", settings.isEager())
                        .addPropertyValue("strict", settings.isStrict())
                        .setInitMethodName("initialize")
                        .setDestroyMethodName("release")
                        .getBeanDefinition());
        log.info("WebDevice registered.");
    }

    private static String name(String name) {
        return format("%s.%s", SETTINGS_PREFIX, name);
    }

    public static Settings settings(Environment environment) {
        return Binder.get(environment)
                .bind(SETTINGS_PREFIX, Settings.class)
                .get();
    }
}
