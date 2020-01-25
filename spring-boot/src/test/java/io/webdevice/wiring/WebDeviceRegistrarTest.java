package io.webdevice.wiring;

import io.webdevice.device.DevicePool;
import io.webdevice.device.WebDevice;
import io.webdevice.support.SimpleDeviceCheck;
import io.webdevice.support.SpringDeviceRegistry;
import org.junit.After;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.type.AnnotationMetadata;

import java.net.URL;

import static io.bestquality.util.MapBuilder.newMap;
import static io.webdevice.net.MaskingClassLoader.classLoaderMasking;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;
import static org.springframework.beans.factory.support.AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class WebDeviceRegistrarTest
        extends ConfigurationPropertiesBinderBasedTest {

    @Mock
    private AnnotationMetadata mockMetadata;
    @Mock(extraInterfaces = ConfigurableListableBeanFactory.class)
    private BeanDefinitionRegistry mockRegistry;
    @Captor
    private ArgumentCaptor<GenericBeanDefinition> definitionCaptor;

    @After
    public void tearDown() {
        MockSettingsBinder.uninstall();
    }

    @Test
    public void shouldBindSettingsFromEnvironmentUsingDefaults()
            throws Exception {
        Settings expected = new Settings()
                .withDefaultDevice(null)
                .withScope(null)
                .withStrict(true)
                .withEager(false)
                .withBaseUrl(null);

        Settings actual = settings(environment);
        assertThat(actual)
                .isEqualTo(expected);
    }

    @Test
    public void shouldBindSettingsFromEnvironmentUsingNonDefaults()
            throws Exception {
        Settings expected = new Settings()
                .withDefaultDevice("Foo")
                .withScope("webdevice")
                .withStrict(false)
                .withEager(true)
                .withBaseUrl(new URL("http://webdevice.io"));

        Settings actual = settings(environmentWith("io/webdevice/wiring/non-defaults.yaml"));
        assertThat(actual)
                .isEqualTo(expected);
    }

    @Test
    public void shouldRegisterSettings()
            throws Exception {
        ConfigurableEnvironment environment = environmentWith("io/webdevice/wiring/scope-only.yaml");
        WebDeviceRegistrar registrar = new WebDeviceRegistrar(environment);

        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);

        verify((ConfigurableListableBeanFactory) mockRegistry)
                .registerScope("webdevice", new WebDeviceScope());
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.Settings"), definitionCaptor.capture());
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.DeviceRegistry"), any());
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.WebDevice"), any());
        verifyNoMoreInteractions(mockRegistry);

        GenericBeanDefinition definition = definitionCaptor.getValue();
        assertThat(definition)
                .isEqualTo(genericBeanDefinition(Settings.class)
                        .getBeanDefinition());
        assertThat(definition.getInstanceSupplier().get())
                .isEqualTo(settings(environment));
    }

    @Test
    public void shouldUseMockBinderFromAnnotation() {
        Settings expected = new Settings();
        MockSettingsBinder.install(expected);

        given(mockMetadata.getAnnotationAttributes(EnableWebDevice.class.getName()))
                .willReturn(newMap(String.class, Object.class)
                        .with("binder", MockSettingsBinder.class)
                        .build());

        WebDeviceRegistrar registrar = new WebDeviceRegistrar(environment);

        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);

        assertThat(registeredSettings())
                .isSameAs(expected);
    }

    @Test
    public void shouldUseMockBinderFromEnvironment()
            throws Exception {
        Settings expected = new Settings();
        MockSettingsBinder.install(expected);

        environment.getPropertySources().addLast(new MapPropertySource("test",
                newMap(String.class, Object.class)
                        .with("webdevice.binder", MockSettingsBinder.class)
                        .build()));

        Thread executor = new Thread(() -> {
            MockSettingsBinder.install(expected);
            try {
                WebDeviceRegistrar registrar = new WebDeviceRegistrar(environment);
                registrar.registerBeanDefinitions(mockMetadata, mockRegistry);
            } finally {
                MockSettingsBinder.uninstall();
            }
        });
        executor.setContextClassLoader(classLoaderMasking(Binder.class));
        executor.start();
        executor.join();

        assertThat(registeredSettings())
                .isSameAs(expected);
    }

    @Test
    public void shouldUseDefaultBinder()
            throws Exception {
        Thread executor = new Thread(() -> {
            try {
                WebDeviceRegistrar registrar = new WebDeviceRegistrar(environment);
                registrar.registerBeanDefinitions(mockMetadata, mockRegistry);
            } finally {
                MockSettingsBinder.uninstall();
            }
        });
        executor.setContextClassLoader(classLoaderMasking(Binder.class));
        executor.start();
        executor.join();

        Settings actual = registeredSettings();
        assertThat(actual)
                .isNotNull();
    }

    @Test
    public void shouldOverrideScopeFromAnnotation() {
        Settings expected = new Settings()
                .withScope("test");
        MockSettingsBinder.install(expected);

        given(mockMetadata.getAnnotationAttributes(EnableWebDevice.class.getName()))
                .willReturn(newMap(String.class, Object.class)
                        .with("binder", MockSettingsBinder.class)
                        .with("scope", "foo")
                        .build());

        WebDeviceRegistrar registrar = new WebDeviceRegistrar(environment);

        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);

        Settings actual = registeredSettings();
        assertThat(actual.getScope())
                .isEqualTo("foo");
    }

    @Test
    public void shouldOverrideDefaultDeviceFromAnnotation() {
        Settings expected = new Settings()
                .withDefaultDevice("test");
        MockSettingsBinder.install(expected);

        given(mockMetadata.getAnnotationAttributes(EnableWebDevice.class.getName()))
                .willReturn(newMap(String.class, Object.class)
                        .with("binder", MockSettingsBinder.class)
                        .with("defaultDevice", "iPhone")
                        .build());

        WebDeviceRegistrar registrar = new WebDeviceRegistrar(environment);

        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);

        Settings actual = registeredSettings();
        assertThat(actual.getDefaultDevice())
                .isEqualTo("iPhone");
    }

    @Test
    public void shouldOverrideEagerFromAnnotation() {
        Settings expected = new Settings()
                .withEager(false);
        MockSettingsBinder.install(expected);

        given(mockMetadata.getAnnotationAttributes(EnableWebDevice.class.getName()))
                .willReturn(newMap(String.class, Object.class)
                        .with("binder", MockSettingsBinder.class)
                        .with("eager", true)
                        .build());

        WebDeviceRegistrar registrar = new WebDeviceRegistrar(environment);

        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);

        Settings actual = registeredSettings();
        assertThat(actual.isEager())
                .isTrue();
    }

    @Test
    public void shouldOverrideStrictFromAnnotation() {
        Settings expected = new Settings()
                .withStrict(true);
        MockSettingsBinder.install(expected);

        given(mockMetadata.getAnnotationAttributes(EnableWebDevice.class.getName()))
                .willReturn(newMap(String.class, Object.class)
                        .with("binder", MockSettingsBinder.class)
                        .with("strict", false)
                        .build());

        WebDeviceRegistrar registrar = new WebDeviceRegistrar(environment);

        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);

        Settings actual = registeredSettings();
        assertThat(actual.isStrict())
                .isFalse();
    }

    @Test
    public void shouldOverrideBaseUrlFromAnnotation()
            throws Exception {
        Settings expected = new Settings()
                .withBaseUrl(new URL("http://test.com"));
        MockSettingsBinder.install(expected);

        given(mockMetadata.getAnnotationAttributes(EnableWebDevice.class.getName()))
                .willReturn(newMap(String.class, Object.class)
                        .with("binder", MockSettingsBinder.class)
                        .with("baseUrl", "https://webdevice.io")
                        .build());

        WebDeviceRegistrar registrar = new WebDeviceRegistrar(environment);

        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);

        Settings actual = registeredSettings();
        assertThat(actual.getBaseUrl())
                .isEqualTo(new URL("https://webdevice.io"));
    }

    @Test
    public void shouldSkipRegisteringDeviceIfAlreadyDefined()
            throws Exception {
        WebDeviceRegistrar registrar = new WebDeviceRegistrar(
                environmentWith("io/webdevice/wiring/direct-pooled-device.yaml"));

        given(mockRegistry.isBeanNameInUse("webdevice.Direct"))
                .willReturn(true);

        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);

        verify((ConfigurableListableBeanFactory) mockRegistry)
                .registerScope("webdevice", new WebDeviceScope());
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.Settings"), any());
        verify(mockRegistry)
                .isBeanNameInUse("webdevice.Direct");
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.DeviceRegistry"), any());
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.WebDevice"), any());
        verifyNoMoreInteractions(mockRegistry);
    }

    @Test
    public void shouldSkipRegisteringPoolForPooledDeviceIfAlreadyDefinedAndAliasPoolWithDeviceName()
            throws Exception {
        ConfigurableEnvironment environment = environmentWith("io/webdevice/wiring/direct-pooled-device.yaml");
        WebDeviceRegistrar registrar = new WebDeviceRegistrar(environment);

        given(mockRegistry.isBeanNameInUse("webdevice.Direct"))
                .willReturn(false);
        given(mockRegistry.isBeanNameInUse("webdevice.Direct-Provider"))
                .willReturn(false);
        given(mockRegistry.isBeanNameInUse("webdevice.Direct-Pool"))
                .willReturn(true);

        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);

        verify((ConfigurableListableBeanFactory) mockRegistry)
                .registerScope("webdevice", new WebDeviceScope());
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.Settings"), any());
        verify(mockRegistry)
                .isBeanNameInUse("webdevice.Direct");
        verify(mockRegistry)
                .isBeanNameInUse("webdevice.Direct-Provider");
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.Direct-Provider"), definitionCaptor.capture());
        verify(mockRegistry)
                .isBeanNameInUse("webdevice.Direct-Pool");
        verify(mockRegistry)
                .registerAlias("webdevice.Direct-Pool", "Direct");
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.DeviceRegistry"), any());
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.WebDevice"), any());
        verifyNoMoreInteractions(mockRegistry);

        // Provider definition
        GenericBeanDefinition provider = definitionCaptor.getValue();
        DeviceDefinition definition = settings(environment)
                .device("Direct");
        assertThat(provider)
                .isEqualTo(definition.build().getBeanDefinition());
    }

    @Test
    public void shouldSkipRegisteringProviderForPooledDeviceIfAlreadyDefinedAndAliasPoolWithDeviceName()
            throws Exception {
        ConfigurableEnvironment environment = environmentWith("io/webdevice/wiring/direct-pooled-device.yaml");
        WebDeviceRegistrar registrar = new WebDeviceRegistrar(environment);

        given(mockRegistry.isBeanNameInUse("webdevice.Direct"))
                .willReturn(false);
        given(mockRegistry.isBeanNameInUse("webdevice.Direct-Provider"))
                .willReturn(true);
        given(mockRegistry.isBeanNameInUse("webdevice.Direct-Pool"))
                .willReturn(false);

        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);

        verify((ConfigurableListableBeanFactory) mockRegistry)
                .registerScope("webdevice", new WebDeviceScope());
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.Settings"), any());
        verify(mockRegistry)
                .isBeanNameInUse("webdevice.Direct");
        verify(mockRegistry)
                .isBeanNameInUse("webdevice.Direct-Provider");
        verify(mockRegistry)
                .isBeanNameInUse("webdevice.Direct-Pool");
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.Direct-Pool"), definitionCaptor.capture());
        verify(mockRegistry)
                .registerAlias("webdevice.Direct-Pool", "Direct");
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.DeviceRegistry"), any());
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.WebDevice"), any());
        verifyNoMoreInteractions(mockRegistry);

        // Pool definition
        GenericBeanDefinition pool = definitionCaptor.getValue();
        assertThat(pool.getBeanClass())
                .isSameAs(DevicePool.class);
        assertThat(pool.getAutowireMode())
                .isSameAs(AUTOWIRE_CONSTRUCTOR);
        assertThat(pool.getDestroyMethodName())
                .isEqualTo("dispose");

        ConstructorArgumentValues values = pool.getConstructorArgumentValues();
        assertThat(values.getArgumentCount())
                .isSameAs(3);
        assertThat(values.getIndexedArgumentValue(0, String.class).getValue())
                .isEqualTo("Direct");
        assertThat(values.getIndexedArgumentValue(1, String.class).getValue())
                .isEqualTo(new RuntimeBeanReference("webdevice.Direct-Provider"));
        assertThat(values.getIndexedArgumentValue(2, String.class).getValue())
                .isInstanceOf(SimpleDeviceCheck.class);
    }

    @Test
    public void shouldRegisterPooledDeviceAndAliasPoolWithDeviceName()
            throws Exception {
        ConfigurableEnvironment environment = environmentWith("io/webdevice/wiring/direct-pooled-device.yaml");
        WebDeviceRegistrar registrar = new WebDeviceRegistrar(environment);

        given(mockRegistry.isBeanNameInUse("webdevice.Direct"))
                .willReturn(false);
        given(mockRegistry.isBeanNameInUse("webdevice.Direct-Provider"))
                .willReturn(false);
        given(mockRegistry.isBeanNameInUse("webdevice.Direct-Pool"))
                .willReturn(false);

        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);

        verify((ConfigurableListableBeanFactory) mockRegistry)
                .registerScope("webdevice", new WebDeviceScope());
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.Settings"), any());
        verify(mockRegistry)
                .isBeanNameInUse("webdevice.Direct");
        verify(mockRegistry)
                .isBeanNameInUse("webdevice.Direct-Provider");
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.Direct-Provider"), definitionCaptor.capture());
        verify(mockRegistry)
                .isBeanNameInUse("webdevice.Direct-Pool");
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.Direct-Pool"), definitionCaptor.capture());
        verify(mockRegistry)
                .registerAlias("webdevice.Direct-Pool", "Direct");
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.DeviceRegistry"), any());
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.WebDevice"), any());
        verifyNoMoreInteractions(mockRegistry);

        // Provider definition
        GenericBeanDefinition provider = definitionCaptor.getAllValues()
                .get(0);
        DeviceDefinition definition = settings(environment)
                .device("Direct");
        assertThat(provider)
                .isEqualTo(definition.build().getBeanDefinition());

        // Pool definition
        GenericBeanDefinition pool = definitionCaptor.getAllValues()
                .get(1);
        assertThat(pool.getBeanClass())
                .isSameAs(DevicePool.class);
        assertThat(pool.getAutowireMode())
                .isSameAs(AUTOWIRE_CONSTRUCTOR);
        assertThat(pool.getDestroyMethodName())
                .isEqualTo("dispose");
        assertThat(pool.getRole())
                .isEqualTo(ROLE_INFRASTRUCTURE);

        ConstructorArgumentValues values = pool.getConstructorArgumentValues();
        assertThat(values.getArgumentCount())
                .isSameAs(3);
        assertThat(values.getIndexedArgumentValue(0, String.class).getValue())
                .isEqualTo("Direct");
        assertThat(values.getIndexedArgumentValue(1, String.class).getValue())
                .isEqualTo(new RuntimeBeanReference("webdevice.Direct-Provider"));
        assertThat(values.getIndexedArgumentValue(2, String.class).getValue())
                .isInstanceOf(SimpleDeviceCheck.class);
    }

    @Test
    public void shouldRegisterUnpooledDeviceAndAliasProviderWithDeviceName()
            throws Exception {
        ConfigurableEnvironment environment = environmentWith("io/webdevice/wiring/direct-unpooled-device.yaml");
        WebDeviceRegistrar registrar = new WebDeviceRegistrar(environment);

        given(mockRegistry.isBeanNameInUse("webdevice.Direct"))
                .willReturn(false);
        given(mockRegistry.isBeanNameInUse("webdevice.Direct-Provider"))
                .willReturn(false);

        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);

        verify((ConfigurableListableBeanFactory) mockRegistry)
                .registerScope("webdevice", new WebDeviceScope());
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.Settings"), any());
        verify(mockRegistry)
                .isBeanNameInUse("webdevice.Direct");
        verify(mockRegistry)
                .isBeanNameInUse("webdevice.Direct-Provider");
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.Direct-Provider"), definitionCaptor.capture());
        verify(mockRegistry)
                .registerAlias("webdevice.Direct-Provider", "Direct");
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.DeviceRegistry"), any());
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.WebDevice"), any());
        verifyNoMoreInteractions(mockRegistry);

        // Provider definition
        GenericBeanDefinition provider = definitionCaptor.getValue();
        DeviceDefinition definition = settings(environment)
                .device("Direct");
        assertThat(provider)
                .isEqualTo(definition.build().getBeanDefinition());
    }

    @Test
    public void shouldRegisterSpringDeviceRegistryInConfiguredScope()
            throws Exception {
        ConfigurableEnvironment environment = environmentWith("io/webdevice/wiring/scope-only.yaml");
        WebDeviceRegistrar registrar = new WebDeviceRegistrar(environment);

        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);

        verify((ConfigurableListableBeanFactory) mockRegistry)
                .registerScope("webdevice", new WebDeviceScope());
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.Settings"), any());
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.DeviceRegistry"), definitionCaptor.capture());
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.WebDevice"), any());
        verifyNoMoreInteractions(mockRegistry);

        GenericBeanDefinition deviceRegistry = definitionCaptor.getValue();
        assertThat(deviceRegistry)
                .isEqualTo(genericBeanDefinition(SpringDeviceRegistry.class)
                        .setScope("application")
                        .setAutowireMode(AUTOWIRE_CONSTRUCTOR)
                        .getBeanDefinition());
    }

    @Test
    public void shouldRegisterWebDeviceInConfiguredScope()
            throws Exception {
        ConfigurableEnvironment environment = environmentWith("io/webdevice/wiring/non-defaults.yaml");
        WebDeviceRegistrar registrar = new WebDeviceRegistrar(environment);

        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);

        verify((ConfigurableListableBeanFactory) mockRegistry)
                .registerScope("webdevice", new WebDeviceScope());
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.Settings"), any());
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.DeviceRegistry"), any());
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.WebDevice"), definitionCaptor.capture());
        verifyNoMoreInteractions(mockRegistry);

        GenericBeanDefinition webDevice = definitionCaptor.getValue();
        assertThat(webDevice)
                .isEqualTo(genericBeanDefinition(WebDevice.class)
                        .setScope("webdevice")
                        .addConstructorArgReference("webdevice.DeviceRegistry")
                        .setAutowireMode(AUTOWIRE_CONSTRUCTOR)
                        .addPropertyValue("baseUrl", new URL("http://webdevice.io"))
                        .addPropertyValue("defaultDevice", "Foo")
                        .addPropertyValue("eager", Boolean.TRUE)
                        .addPropertyValue("strict", Boolean.FALSE)
                        .setInitMethodName("initialize")
                        .setDestroyMethodName("release")
                        .getBeanDefinition());
    }

    private Settings registeredSettings() {
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.Settings"), definitionCaptor.capture());
        GenericBeanDefinition settingsDefinition = definitionCaptor.getValue();
        return (Settings) settingsDefinition
                .getInstanceSupplier()
                .get();
    }
}