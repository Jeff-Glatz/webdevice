package io.webdevice.wiring;

import io.webdevice.device.WebDevice;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
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

public class WebDeviceScope
        implements Scope {
    private static final String NAME = "web-device";
    private final Map<String, List<Object>> instances = new LinkedHashMap<>();

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
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return null;
    }

    /**
     * Reset all instances in the scope.
     *
     * @return {@code true} if items were reset
     */
    public boolean dispose() {
        final AtomicBoolean disposed = new AtomicBoolean(false);
        synchronized (instances) {
            instances.values().stream()
                    .flatMap(Collection::stream)
                    .filter(instance -> {
                        if (instance instanceof WebDevice) {
                            disposed.set(true);
                            return true;
                        }
                        return false;
                    })
                    .map(instance -> (WebDevice) instance)
                    .forEach(WebDevice::release);

            instances.clear();
        }
        return disposed.get();
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

    public static WebDeviceScope registerScope(ConfigurableListableBeanFactory registry) {
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
