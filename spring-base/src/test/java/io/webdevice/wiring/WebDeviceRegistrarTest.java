package io.webdevice.wiring;

import io.webdevice.device.DevicePool;
import io.webdevice.device.DeviceProvider;
import io.webdevice.device.StubDevicePool;
import io.webdevice.device.StubDeviceProvider;
import io.webdevice.device.StubWebDriver;
import io.webdevice.settings.MockSettingsBinder;
import io.webdevice.settings.Settings;
import io.webdevice.support.SpringDeviceRegistry;
import io.webdevice.test.SpringSandboxTest;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;

import static io.webdevice.wiring.WebDeviceScope.namespace;
import static org.assertj.core.api.Assertions.assertThat;

public class WebDeviceRegistrarTest
        extends SpringSandboxTest {

    @Test
    public void shouldLoadFromAllDevices()
            throws Exception {
        sandbox().withEnvironmentFrom("devices/all-devices.yaml")
                .with(WebDeviceRuntime.class)
                .execute(context -> {
                    SpringDeviceRegistry registry = context.getBean(namespace("DeviceRegistry"),
                            SpringDeviceRegistry.class);
                });
    }

    @Test
    public void shouldUseCustomBinderToBindSettingsFromEnvironment()
            throws Exception {
        Settings expected = new Settings();
        sandbox().withEnvironmentFrom("io/webdevice/wiring/binder-only.properties")
                .with(WebDeviceRuntime.class)
                .withInitializer(context -> MockSettingsBinder.install(expected))
                .execute(context -> {
                    Settings actual = context.getBean(namespace("Settings"), Settings.class);
                    assertThat(actual)
                            .isSameAs(expected);
                });
    }

    @Test
    public void shouldRegisterSettings()
            throws Exception {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/scope-only.properties")
                .with(WebDeviceRuntime.class)
                .execute(context -> {
                    Settings actual = context.getBean(namespace("Settings"), Settings.class);
                    assertThat(actual)
                            .isEqualTo(new Settings()
                                    .withScope("application"));
                });
    }

    @Test
    public void shouldSkipRegisteringDeviceIfAlreadyDefined()
            throws Exception {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/direct-pooled-device.properties")
                .withInitializer(context -> context.registerBean("webdevice.Direct",
                        DeviceProvider.class, () -> new StubDeviceProvider("Direct")))
                .with(WebDeviceRuntime.class)
                .execute(context -> {
                    assertThat(context.containsBeanDefinition(namespace("Direct-Provider")))
                            .isFalse();
                    assertThat(context.containsBeanDefinition(namespace("Direct-Pool")))
                            .isFalse();
                    assertThat(context.getAliases(namespace("Direct")))
                            .containsExactly();

                });
    }

    @Test
    public void shouldSkipRegisteringPoolForPooledDeviceIfAlreadyDefinedAndAliasPoolWithDeviceName()
            throws Exception {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/direct-pooled-device.properties")
                .withInitializer(context -> context.registerBean("webdevice.Direct-Pool",
                        DevicePool.class, () -> new StubDevicePool("Direct", StubWebDriver::new)))
                .with(WebDeviceRuntime.class)
                .execute(context -> {
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
    public void shouldSkipRegisteringProviderForPooledDeviceIfAlreadyDefinedAndAliasPoolWithDeviceName()
            throws Exception {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/direct-pooled-device.properties")
                .withInitializer(context -> context.registerBean("webdevice.Direct-Provider",
                        DeviceProvider.class, () -> new StubDeviceProvider("Direct")))
                .with(WebDeviceRuntime.class)
                .execute(context -> {
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
    public void shouldRegisterPooledDeviceAndAliasPoolWithDeviceName()
            throws Exception {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/direct-pooled-device.properties")
                .with(WebDeviceRuntime.class)
                .execute(context -> {
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
    public void shouldRegisterUnpooledDeviceAndAliasProviderWithDeviceName()
            throws Exception {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/direct-not-pooled-device.properties")
                .with(WebDeviceRuntime.class)
                .execute(context -> {
                    assertThat(context.containsBeanDefinition(namespace("Direct-Provider")))
                            .isTrue();
                    assertThat(context.getAliases(namespace("Direct-Provider")))
                            .containsExactly("Direct");
                    assertThat(context.containsBeanDefinition(namespace("Direct-Pool")))
                            .isFalse();
                });
    }

    @Test
    public void shouldRegisterDeviceRegistryInConfiguredScope()
            throws Exception {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/scope-only.properties")
                .with(WebDeviceRuntime.class)
                .execute(context -> {
                    BeanDefinition definition = context.getBeanDefinition(namespace("DeviceRegistry"));
                    assertThat(definition.getScope())
                            .isEqualTo("application");
                });
    }

    @Test
    public void shouldRegisterWebDeviceInConfiguredScope()
            throws Exception {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/non-defaults.properties")
                .with(WebDeviceRuntime.class)
                .execute(context -> {
                    BeanDefinition definition = context.getBeanDefinition(namespace("WebDevice"));
                    assertThat(definition.getScope())
                            .isEqualTo("webdevice");
                });
    }
}
