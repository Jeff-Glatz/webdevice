package io.webdevice.wiring;

import io.cucumber.spring.CucumberTestContext;
import org.junit.Test;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import static io.bestquality.net.MaskingClassLoader.maskingClasses;
import static org.assertj.core.api.Assertions.assertThat;

public class ScopedWiringTest
        extends ApplicationContextTest {

    @Test
    public void shouldCreateInCucumberGlueScopeWhenCucumberTestContextOnClasspath() {
        // CucumberTestContext is on the test classpath
        configuredBy(WebDeviceRuntime.class)
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
        configuredBy(WebDeviceRuntime.class)
                .withClassLoader(maskingClasses(CucumberTestContext.class))
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
        configuredBy(WebDeviceRuntime.class)
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
