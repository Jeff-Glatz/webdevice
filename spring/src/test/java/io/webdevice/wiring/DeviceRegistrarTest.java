package io.webdevice.wiring;

import org.junit.After;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

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

    // TODO: Verify Bean Definitions
    @Test
    public void shouldRegisterPooledDeviceAliasingPoolWithDeviceName()
            throws Exception {
        DeviceRegistrar registrar = new DeviceRegistrar(
                environmentWith("io/webdevice/wiring/direct-pooled-device.yaml"));

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
    }

    // TODO: Verify Bean Definitions
    @Test
    public void shouldRegisterUnpooledDeviceAliasingProviderWithDeviceName()
            throws Exception {
        DeviceRegistrar registrar = new DeviceRegistrar(
                environmentWith("io/webdevice/wiring/direct-unpooled-device.yaml"));

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
    }
}
