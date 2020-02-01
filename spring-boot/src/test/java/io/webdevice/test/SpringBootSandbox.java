package io.webdevice.test;

import io.webdevice.support.YamlPropertySourceFactory;
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

public class SpringBootSandbox {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private ApplicationContextRunner runner = new ApplicationContextRunner();
    private PropertySourceFactory propertySourceFactory = new YamlPropertySourceFactory();

    public SpringBootSandbox withClassLoader(ClassLoader classLoader) {
        runner = runner.withClassLoader(classLoader);
        return this;
    }

    public SpringBootSandbox withClassesIn(URL... urls) {
        return withClassLoader(new URLClassLoader(urls, loaderOf(this)));
    }

    public SpringBootSandbox withClassesIn(String... resources) {
        ClassLoader loader = loaderOf(this);
        return withClassesIn(stream(resources)
                .map(loader::getResource)
                .toArray(URL[]::new));
    }

    public SpringBootSandbox withEnvironmentFrom(String... resources) {
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

    public SpringBootSandbox withSystemProperties(String... pairs) {
        runner = runner.withSystemProperties(pairs);
        return this;
    }

    public SpringBootSandbox withEnvironmentProperties(String... pairs) {
        runner = runner.withPropertyValues(pairs);
        return this;
    }

    public SpringBootSandbox withInitializer(ApplicationContextInitializer<ConfigurableApplicationContext> initializer) {
        runner = runner.withInitializer(initializer);
        return this;
    }

    public SpringBootSandbox withConfiguration(Class<?>... configurations) {
        runner = runner.withUserConfiguration(configurations);
        return this;
    }

    public ApplicationContextRunner run(ContextConsumer<AssertableApplicationContext> consumer) {
        return runner = runner.run(consumer);
    }
}
