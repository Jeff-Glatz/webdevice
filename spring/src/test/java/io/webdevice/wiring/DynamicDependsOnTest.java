package io.webdevice.wiring;

import io.webdevice.device.DeviceProvider;
import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class DynamicDependsOnTest
        extends UnitTest {
    private DynamicDependsOn dependsOn;

    @Mock
    private ConfigurableListableBeanFactory mockBeanFactory;
    @Mock
    private BeanDefinition mockBeanDefinition;

    @Before
    public void setUp() {
        dependsOn = new DynamicDependsOn();
    }

    @Test
    public void shouldEstablishDeviceRegistryDependenciesOnDeviceProviders() {
        String[] providers = {"provider-1", "provider-2"};

        given(mockBeanFactory.getBeanDefinition("webdevice.DeviceRegistry"))
                .willReturn(mockBeanDefinition);
        given(mockBeanFactory.getBeanNamesForType(DeviceProvider.class))
                .willReturn(providers);

        dependsOn.postProcessBeanFactory(mockBeanFactory);

        verify(mockBeanDefinition)
                .setDependsOn(providers);
    }
}
