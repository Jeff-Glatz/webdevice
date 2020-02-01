package io.webdevice.test;

import io.bestquality.lang.CheckedConsumer;
import io.webdevice.settings.MockSettingsBinder;
import io.webdevice.support.YamlPropertySourceFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static io.bestquality.lang.Classes.loaderOf;
import static java.lang.String.valueOf;
import static java.lang.Thread.currentThread;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

public class SpringSandbox {
    private final Properties systemProperties = new Properties(System.getProperties());
    private final List<ApplicationContextInitializer<AnnotationConfigApplicationContext>> initializers = new ArrayList<>();
    private final List<Class<?>> components = new ArrayList<>();
    private final List<String> packages = new ArrayList<>();
    private final ConfigurableEnvironment environment = new StandardEnvironment();

    private PropertySourceFactory propertySourceFactory = new YamlPropertySourceFactory();
    private ClassLoader classLoader;

    public SpringSandbox withSystemProperty(String key, String value) {
        systemProperties.put(key, value);
        return this;
    }

    public SpringSandbox withClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    public SpringSandbox withClassesIn(URL... urls) {
        return withClassLoader(new URLClassLoader(urls, loaderOf(this)));
    }

    public SpringSandbox withClassesIn(String... resources) {
        ClassLoader loader = loaderOf(this);
        return withClassesIn(stream(resources)
                .map(loader::getResource)
                .toArray(URL[]::new));
    }

    public SpringSandbox withPropertySourceFactory(PropertySourceFactory propertySourceFactory) {
        this.propertySourceFactory = propertySourceFactory;
        return this;
    }

    public SpringSandbox withEnvironmentFrom(String... resources)
            throws IOException {
        MutablePropertySources propertySources = environment.getPropertySources();
        for (String resource : resources) {
            propertySources.addLast(propertySourceFactory
                    .createPropertySource(null,
                            new EncodedResource(new ClassPathResource(resource))));
        }
        return this;
    }

    public SpringSandbox withEnvironmentProperties(Map<String, Object> map) {
        MutablePropertySources propertySources = environment.getPropertySources();
        propertySources.addFirst(new MapPropertySource(valueOf(map.hashCode()), map));
        return this;
    }

    public SpringSandbox withConfiguration(Class<?> component) {
        this.components.add(component);
        return this;
    }

    public SpringSandbox with(Class<?>... components) {
        this.components.addAll(asList(components));
        return this;
    }

    public SpringSandbox scan(String basePackage) {
        this.packages.add(basePackage);
        return this;
    }

    public SpringSandbox scan(String... packages) {
        this.packages.addAll(asList(packages));
        return this;
    }

    public SpringSandbox withInitializer(ApplicationContextInitializer<AnnotationConfigApplicationContext> initializer) {
        this.initializers.add(initializer);
        return this;
    }

    public SpringSandbox execute(CheckedConsumer<AnnotationConfigApplicationContext> consumer) {
        execute(() -> {
            try {
                consumer.accept(prepareNewContext());
            } catch (Throwable ex) {
                rethrow(ex);
            }
        });
        return this;
    }

    private AnnotationConfigApplicationContext prepareNewContext() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        try {
            context.registerShutdownHook();
            if (classLoader != null) {
                context.setClassLoader(classLoader);
            }
            context.setEnvironment(environment);
            components.forEach(context::register);
            packages.forEach(context::scan);
            initializers.forEach((initializer) -> initializer.initialize(context));
            context.refresh();
            return context;
        } catch (RuntimeException ex) {
            context.close();
            throw ex;
        }
    }

    private void execute(Runnable command) {
        Properties properties = System.getProperties();
        System.setProperties(systemProperties);
        try {
            if (classLoader == null) {
                try {
                    command.run();
                } finally {
                    MockSettingsBinder.uninstall();
                }
            } else {
                Thread executor = currentThread();
                ClassLoader previous = executor.getContextClassLoader();
                executor.setContextClassLoader(classLoader);
                try {
                    command.run();
                } finally {
                    executor.setContextClassLoader(previous);
                    MockSettingsBinder.uninstall();
                }
            }
        } finally {
            System.setProperties(properties);
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends Throwable> void rethrow(Throwable e)
            throws E {
        throw (E) e;
    }
}
