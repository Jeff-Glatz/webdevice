package io.webdevice.wiring;

import io.webdevice.device.WebDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.format;

public class WebDeviceScope
        implements Scope {
    public static final String NAME = "webdevice";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<String, List<Object>> instances = new LinkedHashMap<>();
    private final Map<String, Runnable> callbacks = new LinkedHashMap<>();

    public boolean isEmpty() {
        return instances.isEmpty();
    }

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        synchronized (instances) {
            Object instance = objectFactory.getObject();
            instances.computeIfAbsent(name, (s) -> new ArrayList<>())
                    .add(instance);
            return instance;
        }
    }

    @Override
    public Object remove(String name) {
        synchronized (instances) {
            return instances.remove(name);
        }
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        log.info("Registering destruction callback for {}", name);
        // TODO: This is a prototype scope, all instances will have the same name
        synchronized (callbacks) {
            callbacks.put(name, callback);
        }
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return null;
    }

    public boolean dispose() {
        synchronized (instances) {
            final AtomicBoolean disposed = new AtomicBoolean(false);
            // Release all WebDevices first
            instances.values().stream()
                    .flatMap(Collection::stream)
                    .filter(instance -> {
                        if (instance instanceof WebDevice) {
                            disposed.set(true);
                            return true;
                        }
                        return false;
                    })
                    .map(WebDevice.class::cast)
                    .forEach(WebDevice::release);
            instances.clear();
            return disposed.get();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebDeviceScope scope = (WebDeviceScope) o;
        return Objects.equals(instances, scope.instances);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instances);
    }

    boolean destructionCallbackRegistered(String name) {
        synchronized (callbacks) {
            return callbacks.containsKey(name);
        }
    }

    boolean safelyDestroy(String name) {
        try {
            synchronized (callbacks) {
                Runnable callback = callbacks.remove(name);
                if (callback != null) {
                    log.debug("Invoking destruction callback for {}", name);
                    callback.run();
                    return true;
                }
            }
        } catch (Exception e) {
            log.warn(format("Failure invoking destruction callback for %s", name), e);
        }
        return false;
    }

    public static String namespace(String name, Object... args) {
        return format("%s.%s", NAME, format(name, args));
    }

    public static WebDeviceScope registerScope(ConfigurableBeanFactory registry) {
        WebDeviceScope scope = new WebDeviceScope();
        registry.registerScope(NAME, scope);
        return scope;
    }

    public static WebDeviceScope scope(ApplicationContext context) {
        if (context instanceof ConfigurableApplicationContext) {
            return (WebDeviceScope) ((ConfigurableApplicationContext) context)
                    .getBeanFactory()
                    .getRegisteredScope(NAME);
        }
        return null;
    }
}
