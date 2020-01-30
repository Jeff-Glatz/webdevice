package io.webdevice.wiring;

import io.webdevice.configurations.DirectPooledDevice;
import io.webdevice.device.DeviceProvider;
import io.webdevice.settings.Settings;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

public class WebDeviceRegistrarTest
        extends ApplicationContextTest {

    @Mock
    private AnnotationMetadata mockMetadata;
    @Mock(extraInterfaces = ConfigurableListableBeanFactory.class)
    private BeanDefinitionRegistry mockRegistry;
    @Captor
    private ArgumentCaptor<GenericBeanDefinition> definitionCaptor;

    @Test
    public void shouldSkipRegisteringDeviceIfAlreadyDefined()
            throws Exception {
        configuredBy(DirectPooledDevice.class)
                // Device already registered
                .withBean("webdevice.Direct", String.class, () -> "Hi!")
                .run(context -> {
                    ConfigurableListableBeanFactory factory = context.getBeanFactory();
                    assertThat(factory.getBean("webdevice.Direct"))
                            .isInstanceOf(String.class);
                    assertThat(factory.containsBeanDefinition("webdevice.Direct-Pool"))
                            .isFalse();
                    assertThat(factory.containsBeanDefinition("webdevice.Direct-Provider"))
                            .isFalse();
                });
    }

    @Test
    public void shouldSkipRegisteringPoolIfAlreadyDefinedAndAliasPoolWithDeviceName()
            throws Exception {
        configuredBy(DirectPooledDevice.class)
                // DevicePool already registered
                .withBean("webdevice.Direct-Pool", String.class, () -> "Hi!")
                .run(context -> {
                    ConfigurableListableBeanFactory factory = context.getBeanFactory();
                    assertThat(factory.getBean("webdevice.Direct-Provider"))
                            .isInstanceOf(DeviceProvider.class);
                    assertThat(factory.getBean("webdevice.Direct-Pool"))
                            .isInstanceOf(String.class);
                    assertThat(factory.getBean("Direct"))
                            .isInstanceOf(String.class);
                    assertThat(factory.getBean("Firefox"))
                            .isInstanceOf(String.class);
                });

//        ConfigurableEnvironment environment = environmentWith("io/webdevice/wiring/direct-pooled-device.yaml");
//        WebDeviceRegistrar registrar = new WebDeviceRegistrar(environment);
//
//        given(mockRegistry.isBeanNameInUse("webdevice.Direct"))
//                .willReturn(false);
//        given(mockRegistry.isBeanNameInUse("webdevice.Direct-Provider"))
//                .willReturn(false);
//        given(mockRegistry.isBeanNameInUse("webdevice.Direct-Pool"))
//                .willReturn(true);
//
//        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);
//
//        verify((ConfigurableListableBeanFactory) mockRegistry)
//                .registerScope("webdevice", new WebDeviceScope());
//        verify(mockRegistry)
//                .registerBeanDefinition(eq("webdevice.Settings"), any());
//        verify(mockRegistry)
//                .isBeanNameInUse("webdevice.Direct");
//        verify(mockRegistry)
//                .isBeanNameInUse("webdevice.Direct-Provider");
//        verify(mockRegistry)
//                .registerBeanDefinition(eq("webdevice.Direct-Provider"), definitionCaptor.capture());
//        verify(mockRegistry)
//                .isBeanNameInUse("webdevice.Direct-Pool");
//        verify(mockRegistry)
//                .registerAlias("webdevice.Direct-Pool", "Direct");
//        verify(mockRegistry)
//                .registerBeanDefinition(eq("webdevice.DeviceRegistry"), any());
//        verify(mockRegistry)
//                .registerBeanDefinition(eq("webdevice.WebDevice"), any());
//        verifyNoMoreInteractions(mockRegistry);
//
//        // Provider definition
//        GenericBeanDefinition provider = definitionCaptor.getValue();
//        DeviceDefinition definition = bindFrom(environment)
//                .device("Direct");
//        assertThat(provider)
//                .isEqualTo(definition.build().getBeanDefinition());
    }
//
//    @Test
//    public void shouldSkipRegisteringProviderForPooledDeviceIfAlreadyDefinedAndAliasPoolWithDeviceName()
//            throws Exception {
//        ConfigurableEnvironment environment = environmentWith("io/webdevice/wiring/direct-pooled-device.yaml");
//        WebDeviceRegistrar registrar = new WebDeviceRegistrar(environment);
//
//        given(mockRegistry.isBeanNameInUse("webdevice.Direct"))
//                .willReturn(false);
//        given(mockRegistry.isBeanNameInUse("webdevice.Direct-Provider"))
//                .willReturn(true);
//        given(mockRegistry.isBeanNameInUse("webdevice.Direct-Pool"))
//                .willReturn(false);
//
//        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);
//
//        verify((ConfigurableListableBeanFactory) mockRegistry)
//                .registerScope("webdevice", new WebDeviceScope());
//        verify(mockRegistry)
//                .registerBeanDefinition(eq("webdevice.Settings"), any());
//        verify(mockRegistry)
//                .isBeanNameInUse("webdevice.Direct");
//        verify(mockRegistry)
//                .isBeanNameInUse("webdevice.Direct-Provider");
//        verify(mockRegistry)
//                .isBeanNameInUse("webdevice.Direct-Pool");
//        verify(mockRegistry)
//                .registerBeanDefinition(eq("webdevice.Direct-Pool"), definitionCaptor.capture());
//        verify(mockRegistry)
//                .registerAlias("webdevice.Direct-Pool", "Direct");
//        verify(mockRegistry)
//                .registerBeanDefinition(eq("webdevice.DeviceRegistry"), any());
//        verify(mockRegistry)
//                .registerBeanDefinition(eq("webdevice.WebDevice"), any());
//        verifyNoMoreInteractions(mockRegistry);
//
//        // Pool definition
//        GenericBeanDefinition pool = definitionCaptor.getValue();
//        assertThat(pool.getBeanClass())
//                .isSameAs(DevicePool.class);
//        assertThat(pool.getAutowireMode())
//                .isSameAs(AUTOWIRE_CONSTRUCTOR);
//        assertThat(pool.getDestroyMethodName())
//                .isEqualTo("dispose");
//
//        ConstructorArgumentValues values = pool.getConstructorArgumentValues();
//        assertThat(values.getArgumentCount())
//                .isSameAs(3);
//        assertThat(values.getIndexedArgumentValue(0, String.class).getValue())
//                .isEqualTo("Direct");
//        assertThat(values.getIndexedArgumentValue(1, String.class).getValue())
//                .isEqualTo(new RuntimeBeanReference("webdevice.Direct-Provider"));
//        assertThat(values.getIndexedArgumentValue(2, String.class).getValue())
//                .isInstanceOf(SimpleDeviceCheck.class);
//    }
//
//    @Test
//    public void shouldRegisterPooledDeviceAndAliasPoolWithDeviceName()
//            throws Exception {
//        ConfigurableEnvironment environment = environmentWith("io/webdevice/wiring/direct-pooled-device.yaml");
//        WebDeviceRegistrar registrar = new WebDeviceRegistrar(environment);
//
//        given(mockRegistry.isBeanNameInUse("webdevice.Direct"))
//                .willReturn(false);
//        given(mockRegistry.isBeanNameInUse("webdevice.Direct-Provider"))
//                .willReturn(false);
//        given(mockRegistry.isBeanNameInUse("webdevice.Direct-Pool"))
//                .willReturn(false);
//
//        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);
//
//        verify((ConfigurableListableBeanFactory) mockRegistry)
//                .registerScope("webdevice", new WebDeviceScope());
//        verify(mockRegistry)
//                .registerBeanDefinition(eq("webdevice.Settings"), any());
//        verify(mockRegistry)
//                .isBeanNameInUse("webdevice.Direct");
//        verify(mockRegistry)
//                .isBeanNameInUse("webdevice.Direct-Provider");
//        verify(mockRegistry)
//                .registerBeanDefinition(eq("webdevice.Direct-Provider"), definitionCaptor.capture());
//        verify(mockRegistry)
//                .isBeanNameInUse("webdevice.Direct-Pool");
//        verify(mockRegistry)
//                .registerBeanDefinition(eq("webdevice.Direct-Pool"), definitionCaptor.capture());
//        verify(mockRegistry)
//                .registerAlias("webdevice.Direct-Pool", "Direct");
//        verify(mockRegistry)
//                .registerBeanDefinition(eq("webdevice.DeviceRegistry"), any());
//        verify(mockRegistry)
//                .registerBeanDefinition(eq("webdevice.WebDevice"), any());
//        verifyNoMoreInteractions(mockRegistry);
//
//        // Provider definition
//        GenericBeanDefinition provider = definitionCaptor.getAllValues()
//                .get(0);
//        DeviceDefinition definition = bindFrom(environment)
//                .device("Direct");
//        assertThat(provider)
//                .isEqualTo(definition.build().getBeanDefinition());
//
//        // Pool definition
//        GenericBeanDefinition pool = definitionCaptor.getAllValues()
//                .get(1);
//        assertThat(pool.getBeanClass())
//                .isSameAs(DevicePool.class);
//        assertThat(pool.getAutowireMode())
//                .isSameAs(AUTOWIRE_CONSTRUCTOR);
//        assertThat(pool.getDestroyMethodName())
//                .isEqualTo("dispose");
//        assertThat(pool.getRole())
//                .isEqualTo(ROLE_INFRASTRUCTURE);
//
//        ConstructorArgumentValues values = pool.getConstructorArgumentValues();
//        assertThat(values.getArgumentCount())
//                .isSameAs(3);
//        assertThat(values.getIndexedArgumentValue(0, String.class).getValue())
//                .isEqualTo("Direct");
//        assertThat(values.getIndexedArgumentValue(1, String.class).getValue())
//                .isEqualTo(new RuntimeBeanReference("webdevice.Direct-Provider"));
//        assertThat(values.getIndexedArgumentValue(2, String.class).getValue())
//                .isInstanceOf(SimpleDeviceCheck.class);
//    }
//
//    @Test
//    public void shouldRegisterUnpooledDeviceAndAliasProviderWithDeviceName()
//            throws Exception {
//        ConfigurableEnvironment environment = environmentWith("io/webdevice/wiring/direct-not-pooled-device.yaml");
//        WebDeviceRegistrar registrar = new WebDeviceRegistrar(environment);
//
//        given(mockRegistry.isBeanNameInUse("webdevice.Direct"))
//                .willReturn(false);
//        given(mockRegistry.isBeanNameInUse("webdevice.Direct-Provider"))
//                .willReturn(false);
//
//        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);
//
//        verify((ConfigurableListableBeanFactory) mockRegistry)
//                .registerScope("webdevice", new WebDeviceScope());
//        verify(mockRegistry)
//                .registerBeanDefinition(eq("webdevice.Settings"), any());
//        verify(mockRegistry)
//                .isBeanNameInUse("webdevice.Direct");
//        verify(mockRegistry)
//                .isBeanNameInUse("webdevice.Direct-Provider");
//        verify(mockRegistry)
//                .registerBeanDefinition(eq("webdevice.Direct-Provider"), definitionCaptor.capture());
//        verify(mockRegistry)
//                .registerAlias("webdevice.Direct-Provider", "Direct");
//        verify(mockRegistry)
//                .registerBeanDefinition(eq("webdevice.DeviceRegistry"), any());
//        verify(mockRegistry)
//                .registerBeanDefinition(eq("webdevice.WebDevice"), any());
//        verifyNoMoreInteractions(mockRegistry);
//
//        // Provider definition
//        GenericBeanDefinition provider = definitionCaptor.getValue();
//        DeviceDefinition definition = bindFrom(environment)
//                .device("Direct");
//        assertThat(provider)
//                .isEqualTo(definition.build().getBeanDefinition());
//    }

    private Settings registeredSettings() {
        verify(mockRegistry)
                .registerBeanDefinition(eq("webdevice.Settings"), definitionCaptor.capture());
        GenericBeanDefinition settingsDefinition = definitionCaptor.getValue();
        return (Settings) settingsDefinition
                .getInstanceSupplier()
                .get();
    }

}
