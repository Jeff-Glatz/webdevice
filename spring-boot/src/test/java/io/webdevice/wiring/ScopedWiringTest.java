package io.webdevice.wiring;

import io.cucumber.spring.CucumberTestContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

public class ScopedWiringTest {
    private ApplicationContextRunner runner;

    @Before
    public void setUp() {
        runner = new ApplicationContextRunner();
    }

    @Test
    public void shouldCreateInCucumberGlueScopeWhenCucumberTestContextOnClasspath() {
        // CucumberTestContext is on the test classpath
        runner.withUserConfiguration(WebDeviceRuntime.class)
                .run(context -> {
                    ConfigurableListableBeanFactory factory = context.getBeanFactory();
                    assertThat(factory.getBeanDefinition("webdevice.WebDevice").getScope())
                            .isEqualTo("cucumber-glue");
                    assertThat(factory.getBeanDefinition("webdevice.DeviceRegistry").getScope())
                            .isEqualTo("cucumber-glue");
                });
    }

    @Test
    public void shouldCreateInWebDeviceScopeWhenCucumberTestContextNotOnClasspath() {
        // CucumberTestContext is not on the test classpath
        runner.withUserConfiguration(WebDeviceRuntime.class)
                .withClassLoader(new FilteredClassLoader(CucumberTestContext.class))
                .run(context -> {
                    ConfigurableListableBeanFactory factory = context.getBeanFactory();
                    assertThat(factory.getBeanDefinition("webdevice.WebDevice").getScope())
                            .isEqualTo("webdevice");
                    assertThat(factory.getBeanDefinition("webdevice.DeviceRegistry").getScope())
                            .isEqualTo("webdevice");
                });
    }

    @Test
    public void shouldCreateInConfiguredScopeWhenCucumberTestContextOnClasspath() {
        // CucumberTestContext is on the test classpath
        runner.withUserConfiguration(WebDeviceRuntime.class)
                .withPropertyValues("webdevice.scope=application")
                .run(context -> {
                    ConfigurableListableBeanFactory factory = context.getBeanFactory();
                    assertThat(factory.getBeanDefinition("webdevice.WebDevice").getScope())
                            .isEqualTo("application");
                    assertThat(factory.getBeanDefinition("webdevice.DeviceRegistry").getScope())
                            .isEqualTo("application");
                });
    }
}
