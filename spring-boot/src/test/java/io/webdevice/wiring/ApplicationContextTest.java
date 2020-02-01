package io.webdevice.wiring;

import io.webdevice.support.YamlPropertySourceFactory;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.ContextConsumer;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import static io.bestquality.lang.Classes.loaderOf;
import static java.util.Arrays.stream;

public class ApplicationContextTest {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationContextRunner runner;
    private PropertySourceFactory propertySourceFactory;

    @Before
    public void setUp() {
        propertySourceFactory = new YamlPropertySourceFactory();
        runner = new ApplicationContextRunner();
    }

    protected ApplicationContextTest sandbox() {
        return this;
    }

    protected ApplicationContextTest withClassLoader(ClassLoader classLoader) {
        runner = runner.withClassLoader(classLoader);
        return this;
    }

    protected ApplicationContextTest withClassesIn(URL... urls) {
        return withClassLoader(new URLClassLoader(urls, loaderOf(this)));
    }

    protected ApplicationContextTest withClassesIn(String... resources) {
        ClassLoader loader = loaderOf(this);
        return withClassesIn(stream(resources)
                .map(loader::getResource)
                .toArray(URL[]::new));
    }

    protected ApplicationContextTest withEnvironmentFrom(String... resources) {
        runner = runner.withInitializer(context -> {
            MutablePropertySources sources = context.getEnvironment().getPropertySources();
            for (String resource : resources) {
                log.info("Adding PropertySource for {}", resource);
                try {
                    sources.addFirst(propertySourceFactory.createPropertySource(null,
                            new EncodedResource(new ClassPathResource(resource))));
                } catch (IOException e) {
                    throw new ApplicationContextException("Failure loading property resources", e);
                }
            }
            ConfigurationPropertySources.attach(context.getEnvironment());
        });
        return this;
    }

    protected ApplicationContextTest withSystemProperties(String... pairs) {
        runner = runner.withSystemProperties(pairs);
        return this;
    }

    protected ApplicationContextTest withEnvironmentProperties(String... pairs) {
        runner = runner.withPropertyValues(pairs);
        return this;
    }

    protected ApplicationContextTest withInitializer(ApplicationContextInitializer<ConfigurableApplicationContext> initializer) {
        runner = runner.withInitializer(initializer);
        return this;
    }

    protected ApplicationContextTest withConfiguration(Class<?>... configurations) {
        runner = runner.withUserConfiguration(configurations);
        return this;
    }

    protected ApplicationContextRunner run(ContextConsumer<AssertableApplicationContext> consumer) {
        return runner = runner.run(consumer);
    }
}
