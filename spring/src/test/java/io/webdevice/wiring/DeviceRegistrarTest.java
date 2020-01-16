package io.webdevice.wiring;

import io.webdevice.device.DevicePool;
import io.webdevice.support.SimpleDeviceCheck;
import org.junit.After;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;

import static io.webdevice.wiring.Settings.settings;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.beans.factory.support.AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR;

public class DeviceRegistrarTest
        extends EnvironmentBasedTest {

    @Mock
    private AnnotationMetadata mockMetadata;
    @Mock
    private BeanDefinitionRegistry mockRegistry;
    @Captor
    private ArgumentCaptor<GenericBeanDefinition> definitionCaptor;

    @After
    public void tearDown() throws Exception {
        // AnnotationMetadata is never used
        verifyNoInteractions(mockMetadata);
    }

    @Test
    public void shouldSkipDevicesAlreadyDefined()
            throws Exception {
        DeviceRegistrar registrar = new DeviceRegistrar(
                environmentWith("io/webdevice/wiring/direct-pooled-device.yaml"));

        given(mockRegistry.isBeanNameInUse("Direct"))
                .willReturn(true);

        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);

        verify(mockRegistry)
                .isBeanNameInUse("Direct");
        verifyNoMoreInteractions(mockRegistry);
    }

    @Test
    public void shouldRegisterPooledDeviceAliasingPoolWithDeviceName()
            throws Exception {
        StandardEnvironment environment = environmentWith("io/webdevice/wiring/direct-pooled-device.yaml");
        DeviceRegistrar registrar = new DeviceRegistrar(environment);

        given(mockRegistry.isBeanNameInUse("Direct"))
                .willReturn(false);
        given(mockRegistry.isBeanNameInUse("Direct-provider"))
                .willReturn(false);
        given(mockRegistry.isBeanNameInUse("Direct-pool"))
                .willReturn(false);

        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);

        verify(mockRegistry)
                .isBeanNameInUse("Direct");
        verify(mockRegistry)
                .isBeanNameInUse("Direct-provider");
        verify(mockRegistry)
                .registerBeanDefinition(eq("Direct-provider"), definitionCaptor.capture());
        verify(mockRegistry)
                .isBeanNameInUse("Direct-pool");
        verify(mockRegistry)
                .registerBeanDefinition(eq("Direct-pool"), definitionCaptor.capture());
        verify(mockRegistry)
                .registerAlias("Direct-pool", "Direct");
        verifyNoMoreInteractions(mockRegistry);

        // Provider definition
        GenericBeanDefinition provider = definitionCaptor.getAllValues()
                .get(0);
        DeviceSettings settings = settings(environment)
                .device("Direct");
        assertThat(provider)
                .isEqualTo(settings.definitionOf().getBeanDefinition());

        // Pool definition
        GenericBeanDefinition pool = definitionCaptor.getAllValues()
                .get(1);
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
                .isEqualTo(new RuntimeBeanReference("Direct-provider"));
        assertThat(values.getIndexedArgumentValue(2, String.class).getValue())
                .isInstanceOf(SimpleDeviceCheck.class);
    }

    @Test
    public void shouldRegisterUnpooledDeviceAliasingProviderWithDeviceName()
            throws Exception {
        StandardEnvironment environment = environmentWith("io/webdevice/wiring/direct-unpooled-device.yaml");
        DeviceRegistrar registrar = new DeviceRegistrar(environment);

        given(mockRegistry.isBeanNameInUse("Direct"))
                .willReturn(false);
        given(mockRegistry.isBeanNameInUse("Direct-provider"))
                .willReturn(false);

        registrar.registerBeanDefinitions(mockMetadata, mockRegistry);

        verify(mockRegistry)
                .isBeanNameInUse("Direct");
        verify(mockRegistry)
                .isBeanNameInUse("Direct-provider");
        verify(mockRegistry)
                .registerBeanDefinition(eq("Direct-provider"), definitionCaptor.capture());
        verify(mockRegistry)
                .registerAlias("Direct-provider", "Direct");
        verifyNoMoreInteractions(mockRegistry);

        // Provider definition
        GenericBeanDefinition provider = definitionCaptor.getValue();
        DeviceSettings settings = settings(environment)
                .device("Direct");
        assertThat(provider)
                .isEqualTo(settings.definitionOf().getBeanDefinition());
    }
}
