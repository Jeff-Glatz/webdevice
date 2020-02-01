package io.webdevice.wiring;

import io.webdevice.device.DevicePool;
import io.webdevice.device.DeviceProvider;
import io.webdevice.device.StubDevicePool;
import io.webdevice.device.StubDeviceProvider;
import io.webdevice.device.StubWebDriver;
import io.webdevice.settings.MockSettingsBinder;
import io.webdevice.settings.Settings;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.SimpleThreadScope;

import static io.webdevice.settings.SettingsMaker.allDevices;
import static io.webdevice.wiring.WebDeviceScope.namespace;
import static org.assertj.core.api.Assertions.assertThat;

public class WebDeviceRegistrarTest
        extends ApplicationContextTest
        implements WebDeviceRegistrarUseCases {

    @Test
    public void shouldLoadFromAllDevices() {
        sandbox().withEnvironmentFrom("devices/all-devices.yaml")
                .withSystemProperties("saucelabs_username=saucy", "saucelabs_accessKey=2secret4u")
                .withConfiguration(WebDeviceRuntime.class)
                .run(context -> {
                    Settings actual = context.getBean(namespace("Settings"), Settings.class);
                    assertThat(actual)
                            .isEqualTo(allDevices());
                });
    }

    @Test
    public void shouldUseCustomBinderToBindSettingsFromEnvironment() {
        Settings expected = new Settings();
        sandbox().withEnvironmentFrom("io/webdevice/wiring/binder-only.properties")
                .withConfiguration(WebDeviceRuntime.class)
                .withInitializer(context -> MockSettingsBinder.install(expected))
                .run(context -> {
                    Settings actual = context.getBean(namespace("Settings"), Settings.class);
                    assertThat(actual)
                            .isSameAs(expected);
                });
    }

    @Test
    public void shouldRegisterSettings() {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/scope-only.properties")
                .withConfiguration(WebDeviceRuntime.class)
                .run(context -> {
                    Settings actual = context.getBean(namespace("Settings"), Settings.class);
                    assertThat(actual)
                            .isEqualTo(new Settings()
                                    .withScope("application"));
                });
    }

    @Test
    public void shouldSkipRegisteringDeviceIfAlreadyDefined() {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/direct-pooled-device.properties")
                .withConfiguration(WebDeviceRuntime.class)
                .withInitializer(context -> ((AnnotationConfigApplicationContext) context)
                        .registerBean("webdevice.Direct",
                                DeviceProvider.class,
                                () -> new StubDeviceProvider("Direct")))
                .run(context -> {
                    assertThat(context.containsBeanDefinition(namespace("Direct-Provider")))
                            .isFalse();
                    assertThat(context.containsBeanDefinition(namespace("Direct-Pool")))
                            .isFalse();
                    assertThat(context.getAliases(namespace("Direct")))
                            .containsExactly();

                });
    }

    @Test
    public void shouldSkipRegisteringPoolForPooledDeviceIfAlreadyDefinedAndAliasPoolWithDeviceName() {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/direct-pooled-device.properties")
                .withConfiguration(WebDeviceRuntime.class)
                .withInitializer(context -> ((AnnotationConfigApplicationContext) context)
                        .registerBean("webdevice.Direct-Pool", DevicePool.class,
                                () -> new StubDevicePool("Direct", StubWebDriver::new)))
                .run(context -> {
                    assertThat(context.containsBeanDefinition(namespace("Direct-Provider")))
                            .isTrue();
                    assertThat(context.getAliases(namespace("Direct-Provider")))
                            .containsExactly();

                    assertThat(context.containsBeanDefinition(namespace("Direct-Pool")))
                            .isTrue();
                    assertThat(context.getBean(namespace("Direct-Pool")))
                            .isInstanceOf(StubDevicePool.class);
                    assertThat(context.getAliases(namespace("Direct-Pool")))
                            .containsExactly("Direct");
                });
    }

    @Test
    public void shouldSkipRegisteringProviderForPooledDeviceIfAlreadyDefinedAndAliasPoolWithDeviceName() {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/direct-pooled-device.properties")
                .withConfiguration(WebDeviceRuntime.class)
                .withInitializer(context -> ((AnnotationConfigApplicationContext) context)
                        .registerBean("webdevice.Direct-Provider",
                                DeviceProvider.class,
                                () -> new StubDeviceProvider("Direct")))
                .run(context -> {
                    assertThat(context.containsBeanDefinition(namespace("Direct-Provider")))
                            .isTrue();
                    assertThat(context.getBean(namespace("Direct-Provider")))
                            .isInstanceOf(StubDeviceProvider.class);
                    assertThat(context.getAliases(namespace("Direct-Provider")))
                            .containsExactly();

                    assertThat(context.containsBeanDefinition(namespace("Direct-Pool")))
                            .isTrue();
                    assertThat(context.getAliases(namespace("Direct-Pool")))
                            .containsExactly("Direct");
                });
    }

    @Test
    public void shouldRegisterPooledDeviceAndAliasPoolWithDeviceName() {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/direct-pooled-device.properties")
                .withConfiguration(WebDeviceRuntime.class)
                .run(context -> {
                    assertThat(context.containsBeanDefinition(namespace("Direct-Provider")))
                            .isTrue();
                    assertThat(context.getAliases(namespace("Direct-Provider")))
                            .containsExactly();

                    assertThat(context.containsBeanDefinition(namespace("Direct-Pool")))
                            .isTrue();
                    assertThat(context.getAliases(namespace("Direct-Pool")))
                            .containsExactly("Direct");
                });
    }

    @Test
    public void shouldRegisterUnPooledDeviceAndAliasProviderWithDeviceName() {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/direct-not-pooled-device.properties")
                .withConfiguration(WebDeviceRuntime.class)
                .run(context -> {
                    assertThat(context.containsBeanDefinition(namespace("Direct-Provider")))
                            .isTrue();
                    assertThat(context.getAliases(namespace("Direct-Provider")))
                            .containsExactly("Direct");
                    assertThat(context.containsBeanDefinition(namespace("Direct-Pool")))
                            .isFalse();
                });
    }

    @Test
    public void shouldRegisterWebDeviceAndDeviceRegistryInConfiguredScope() {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/scope-only.properties")
                .withConfiguration(WebDeviceRuntime.class)
                .run(context -> {
                    ConfigurableListableBeanFactory factory = context.getBeanFactory();

                    BeanDefinition definition = factory.getBeanDefinition(namespace("DeviceRegistry"));
                    assertThat(definition.getScope())
                            .isEqualTo("application");

                    definition = factory.getBeanDefinition(namespace("WebDevice"));
                    assertThat(definition.getScope())
                            .isEqualTo("application");
                });
    }

    @Test
    public void shouldRegisterWebDeviceAndDeviceRegistryInDefaultScope() {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/non-defaults.properties")
                .withConfiguration(WebDeviceRuntime.class)
                .run(context -> {
                    ConfigurableListableBeanFactory factory = context.getBeanFactory();

                    BeanDefinition definition = factory.getBeanDefinition(namespace("DeviceRegistry"));
                    assertThat(definition.getScope())
                            .isEqualTo("webdevice");

                    definition = factory.getBeanDefinition(namespace("WebDevice"));
                    assertThat(definition.getScope())
                            .isEqualTo("webdevice");
                });
    }

    @Test
    public void shouldRegisterWebDeviceAndDeviceRegistryInCucumberScope() {
        sandbox().withClassesIn("stubs/cucumber-stub.jar")
                .withConfiguration(WebDeviceRuntime.class)
                .withInitializer(context -> context.getBeanFactory()
                        .registerScope("cucumber-glue", new SimpleThreadScope()))
                .run(context -> {
                    ConfigurableListableBeanFactory factory = context.getBeanFactory();

                    BeanDefinition definition = factory.getBeanDefinition(namespace("DeviceRegistry"));
                    assertThat(definition.getScope())
                            .isEqualTo("cucumber-glue");

                    definition = factory.getBeanDefinition(namespace("WebDevice"));
                    assertThat(definition.getScope())
                            .isEqualTo("cucumber-glue");
                });
    }
}
