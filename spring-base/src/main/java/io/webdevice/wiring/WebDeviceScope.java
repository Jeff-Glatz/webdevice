package io.webdevice.wiring;

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

import static java.lang.String.format;

/**
 * The default scope in which {@link io.webdevice.device.WebDevice} and
 * {@link io.webdevice.device.DeviceRegistry} instances are created. They
 * are both Flyweights allowing many instances to be created and used
 * simultaneously. Given this, the {@link WebDeviceScope} behaves like the
 * built-in {@code prototype} scope, but it works in conjuction with the
 */
public class WebDeviceScope
        implements Scope {
    public static final String NAME = "webdevice";
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<String, List<Object>> instances = new LinkedHashMap<>();
    private final Map<String, List<Runnable>> callbacks = new LinkedHashMap<>();

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
        synchronized (callbacks) {
            callbacks.computeIfAbsent(name, (s) -> new ArrayList<>())
                    .add(callback);
        }
    }

    @Override
    public Object resolveContextualObject(String key) {
        // This scope offers no well-known contextual references
        return null;
    }

    @Override
    public String getConversationId() {
        return NAME;
    }

    /**
     * Disposes this {@link Scope} by running all registered destruction callbacks
     * then clearing all tracked instances. This {@link Scope} is ready for re-use
     * immediately after invocation of this method.
     *
     * @return {@code true} if at least one destruction callback was invoked while
     *         disposing; {@code false} otherwise.
     */
    public boolean dispose() {
        synchronized (callbacks) {
            try {
                if (!callbacks.isEmpty()) {
                    callbacks.values().stream()
                            .flatMap(Collection::stream)
                            .forEach(this::invoke);
                    return true;
                }
                return false;
            } finally {
                instances.clear();
                callbacks.clear();
            }
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
            return callbacks.containsKey(name) &&
                    !callbacks.get(name).isEmpty();
        }
    }

    private boolean invoke(Runnable callback) {
        try {
            log.debug("Invoking destruction callback ...");
            callback.run();
            return true;
        } catch (Exception e) {
            log.warn("Failure invoking destruction callback.", e);
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
