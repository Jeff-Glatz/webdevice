package io.webdevice.wiring;

import io.webdevice.device.DevicePool;
import io.webdevice.device.WebDevice;
import io.webdevice.support.AnnotationAttributes;
import io.webdevice.support.SimpleDeviceCheck;
import io.webdevice.support.SpringDeviceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.net.URL;

import static io.webdevice.support.AnnotationAttributes.attributesOf;
import static io.webdevice.wiring.WebDeviceScope.registerScope;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;
import static org.springframework.beans.factory.support.AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;
import static org.springframework.util.ClassUtils.forName;
import static org.springframework.util.ClassUtils.isPresent;

public class WebDeviceRegistrar
        implements ImportBeanDefinitionRegistrar {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ConfigurableEnvironment environment;

    @Autowired
    public WebDeviceRegistrar(Environment environment) {
        // Spring implodes when used directly in constructor
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        Settings settings = settings(metadata);
        registerScope((ConfigurableListableBeanFactory) registry);
        registerSettings(settings, registry);
        registerDevices(settings, registry);
        registerDeviceRegistry(settings, registry);
        registerWebDevice(settings, registry);
    }

    private Settings applySettingsFromAnnotation(Settings settings, AnnotationAttributes attributes) {
        if (attributes != null) {
            return settings.withScope(attributes.valueOf("scope", settings::getScope))
                    .withDefaultDevice(attributes.valueOf("defaultDevice", settings::getDefaultDevice))
                    .withEager(attributes.valueOf("eager", Boolean.class, settings::isEager))
                    .withStrict(attributes.valueOf("strict", Boolean.class, settings::isStrict))
                    .withBaseUrl(attributes.valueOf("baseUrl", String.class, URL::new, settings::getBaseUrl));
        }
        return settings;
    }

    private Settings settings(AnnotationMetadata metadata) {
        AnnotationAttributes attributes = attributesOf(EnableWebDevice.class, metadata);
        SettingsBinder binder = attributes.valueOf("binder", Class.class,
                (impl) -> {
                    // Explicitly specified binders take precedence
                    return (impl != SettingsBinder.class)
                            ? (SettingsBinder) impl.getDeclaredConstructor().newInstance()
                            : null;
                },
                () -> {
                    // Attempt to load the preferred binder using Spring Boot.
                    if (isPresent("org.springframework.boot.context.properties.bind.Binder", null)) {
                        // Spring Boot is available, now check if the webdevice-spring-boot module is present
                        if (isPresent("io.webdevice.wiring.ConfigurationPropertiesBinder", null)) {
                            return (SettingsBinder) forName("io.webdevice.wiring.ConfigurationPropertiesBinder", null)
                                    .getDeclaredConstructor()
                                    .newInstance();
                        } else {
                            log.warn("Did you know that there is a Spring Boot module available? See https://webdevice.io for details!");
                        }
                    }
                    // Spring Boot is not available, load from Environment or use default
                    Class<?> binderClass = environment.getProperty("webdevice.binder", Class.class,
                            DefaultSettingsBinder.class);
                    return (SettingsBinder) binderClass
                            .getDeclaredConstructor()
                            .newInstance();
                });
        return applySettingsFromAnnotation(binder.from(environment), attributes);
    }

    private String maybeRegisterProvider(DeviceDefinition device, BeanDefinitionRegistry registry) {
        String provider = WebDeviceScope.namespace("%s-Provider", device.getName());
        if (!registry.isBeanNameInUse(provider)) {
            log.info("Registering WebDeviceProvider definition named {}", provider);
            registry.registerBeanDefinition(provider,
                    device.build()
                            .getBeanDefinition());
        }
        return provider;
    }

    private String maybeRegisterPool(String provider, DeviceDefinition device, BeanDefinitionRegistry registry) {
        String pool = WebDeviceScope.namespace("%s-Pool", device.getName());
        if (!registry.isBeanNameInUse(pool)) {
            log.info("Registering WebDevicePool definition named {}", pool);
            registry.registerBeanDefinition(pool,
                    genericBeanDefinition(DevicePool.class)
                            .addConstructorArgValue(device.getName())
                            .addConstructorArgReference(provider)
                            .addConstructorArgValue(new SimpleDeviceCheck<>())
                            .setAutowireMode(AUTOWIRE_CONSTRUCTOR)
                            .setDestroyMethodName("dispose")
                            .setRole(ROLE_INFRASTRUCTURE)
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
        registry.registerBeanDefinition(WebDeviceScope.namespace("Settings"),
                genericBeanDefinition(Settings.class, () -> settings)
                        .getBeanDefinition());
        log.info("Settings registered.");
    }

    private void registerDevices(Settings settings, BeanDefinitionRegistry registry) {
        log.info("Registering Devices ...");
        settings.devices()
                .filter(device -> {
                    boolean defined = registry.isBeanNameInUse(WebDeviceScope.namespace(device.getName()));
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
        String scope = settings.getScope();
        log.info("Registering DeviceRegistry in {} scope ...", scope);
        registry.registerBeanDefinition(WebDeviceScope.namespace("DeviceRegistry"),
                genericBeanDefinition(SpringDeviceRegistry.class)
                        .setScope(scope)
                        .setAutowireMode(AUTOWIRE_CONSTRUCTOR)
                        .getBeanDefinition());
        log.info("DeviceRegistry registered.");
    }

    private void registerWebDevice(Settings settings, BeanDefinitionRegistry registry) {
        String scope = settings.getScope();
        log.info("Registering WebDevice in {} scope ...", scope);
        registry.registerBeanDefinition(WebDeviceScope.namespace("WebDevice"),
                genericBeanDefinition(WebDevice.class)
                        .setScope(scope)
                        .addConstructorArgReference(WebDeviceScope.namespace("DeviceRegistry"))
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
}
