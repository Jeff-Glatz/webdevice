package io.webdevice.wiring;

import io.webdevice.device.DevicePool;
import io.webdevice.device.DeviceProvider;
import io.webdevice.device.StubDevicePool;
import io.webdevice.device.StubDeviceProvider;
import io.webdevice.device.StubWebDriver;
import io.webdevice.settings.MockSettingsBinder;
import io.webdevice.settings.Settings;
import io.webdevice.test.SpringSandboxTest;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.support.SimpleThreadScope;

import static io.bestquality.util.MapBuilder.mapOf;
import static io.webdevice.settings.SettingsMaker.allDevices;
import static io.webdevice.wiring.WebDeviceScope.namespace;
import static org.assertj.core.api.Assertions.assertThat;

public class WebDeviceRegistrarTest
        extends SpringSandboxTest
        implements WebDeviceRegistrarUseCases {

    @Override
    @Test
    public void shouldLoadFromAllDevices()
            throws Exception {
        sandbox().withEnvironmentFrom("devices/all-devices.yaml")
                .withEnvironmentProperties(mapOf(String.class, Object.class)
                        .with("saucelabs_username", "saucy")
                        .with("saucelabs_accessKey", "2secret4u")
                        .build())
                .withConfiguration(WebDeviceRuntime.class)
                .execute(context -> {
                    Settings actual = context.getBean(namespace("Settings"), Settings.class);
                    assertThat(actual)
                            .isEqualTo(allDevices());
                });
    }

    @Override
    @Test
    public void shouldUseCustomBinderToBindSettingsFromEnvironment()
            throws Exception {
        Settings expected = new Settings();
        sandbox().withEnvironmentFrom("io/webdevice/wiring/binder-only.properties")
                .withConfiguration(WebDeviceRuntime.class)
                .withInitializer(context -> MockSettingsBinder.install(expected))
                .execute(context -> {
                    Settings actual = context.getBean(namespace("Settings"), Settings.class);
                    assertThat(actual)
                            .isSameAs(expected);
                });
    }

    @Override
    @Test
    public void shouldRegisterSettings()
            throws Exception {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/scope-only.properties")
                .withConfiguration(WebDeviceRuntime.class)
                .execute(context -> {
                    Settings actual = context.getBean(namespace("Settings"), Settings.class);
                    assertThat(actual)
                            .isEqualTo(new Settings()
                                    .withScope("application"));
                });
    }

    @Override
    @Test
    public void shouldSkipRegisteringDeviceIfAlreadyDefined()
            throws Exception {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/direct-pooled-device.properties")
                .withInitializer(context -> context.registerBean("webdevice.Direct",
                        DeviceProvider.class, () -> new StubDeviceProvider("Direct")))
                .withConfiguration(WebDeviceRuntime.class)
                .execute(context -> {
                    assertThat(context.containsBeanDefinition(namespace("Direct-Provider")))
                            .isFalse();
                    assertThat(context.containsBeanDefinition(namespace("Direct-Pool")))
                            .isFalse();
                    assertThat(context.getAliases(namespace("Direct")))
                            .containsExactly();

                });
    }

    @Override
    @Test
    public void shouldSkipRegisteringPoolForPooledDeviceIfAlreadyDefinedAndAliasPoolWithDeviceName()
            throws Exception {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/direct-pooled-device.properties")
                .withInitializer(context -> context.registerBean("webdevice.Direct-Pool",
                        DevicePool.class, () -> new StubDevicePool("Direct", StubWebDriver::new)))
                .withConfiguration(WebDeviceRuntime.class)
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

    @Override
    @Test
    public void shouldSkipRegisteringProviderForPooledDeviceIfAlreadyDefinedAndAliasPoolWithDeviceName()
            throws Exception {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/direct-pooled-device.properties")
                .withInitializer(context -> context.registerBean("webdevice.Direct-Provider",
                        DeviceProvider.class, () -> new StubDeviceProvider("Direct")))
                .withConfiguration(WebDeviceRuntime.class)
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

    @Override
    @Test
    public void shouldRegisterPooledDeviceAndAliasPoolWithDeviceName()
            throws Exception {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/direct-pooled-device.properties")
                .withConfiguration(WebDeviceRuntime.class)
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

    @Override
    @Test
    public void shouldRegisterUnPooledDeviceAndAliasProviderWithDeviceName()
            throws Exception {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/direct-not-pooled-device.properties")
                .withConfiguration(WebDeviceRuntime.class)
                .execute(context -> {
                    assertThat(context.containsBeanDefinition(namespace("Direct-Provider")))
                            .isTrue();
                    assertThat(context.getAliases(namespace("Direct-Provider")))
                            .containsExactly("Direct");
                    assertThat(context.containsBeanDefinition(namespace("Direct-Pool")))
                            .isFalse();
                });
    }

    @Override
    @Test
    public void shouldRegisterWebDeviceAndDeviceRegistryInConfiguredScope()
            throws Exception {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/scope-only.properties")
                .withConfiguration(WebDeviceRuntime.class)
                .execute(context -> {
                    BeanDefinition definition = context.getBeanDefinition(namespace("DeviceRegistry"));
                    assertThat(definition.getScope())
                            .isEqualTo("application");

                    definition = context.getBeanDefinition(namespace("WebDevice"));
                    assertThat(definition.getScope())
                            .isEqualTo("application");
                });
    }

    @Override
    @Test
    public void shouldRegisterWebDeviceAndDeviceRegistryInDefaultScope()
            throws Exception {
        sandbox().withEnvironmentFrom("io/webdevice/wiring/non-defaults.properties")
                .withConfiguration(WebDeviceRuntime.class)
                .execute(context -> {
                    BeanDefinition definition = context.getBeanDefinition(namespace("DeviceRegistry"));
                    assertThat(definition.getScope())
                            .isEqualTo("webdevice");

                    definition = context.getBeanDefinition(namespace("WebDevice"));
                    assertThat(definition.getScope())
                            .isEqualTo("webdevice");
                });
    }

    @Override
    @Test
    public void shouldRegisterWebDeviceAndDeviceRegistryInCucumberScope() {
        sandbox().withConfiguration(WebDeviceRuntime.class)
                .withInitializer(context -> context.getBeanFactory()
                        .registerScope("cucumber-glue", new SimpleThreadScope()))
                .withClassesIn("stubs/cucumber-stub.jar")
                .execute(context -> {
                    BeanDefinition definition = context.getBeanDefinition(namespace("DeviceRegistry"));
                    assertThat(definition.getScope())
                            .isEqualTo("cucumber-glue");

                    definition = context.getBeanDefinition(namespace("WebDevice"));
                    assertThat(definition.getScope())
                            .isEqualTo("cucumber-glue");
                });
    }
}
