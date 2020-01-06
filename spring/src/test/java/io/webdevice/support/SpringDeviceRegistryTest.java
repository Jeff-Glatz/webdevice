package io.webdevice.support;

import io.webdevice.device.Device;
import io.webdevice.device.DeviceNotProvidedException;
import io.webdevice.device.DeviceProvider;
import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import static io.webdevice.device.Devices.directDevice;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class SpringDeviceRegistryTest
        extends UnitTest {
    @Mock
    private BeanFactory mockBeanFactory;
    private SpringDeviceRegistry registry;

    @Mock
    private WebDriver mockWebDriver;
    private Device<WebDriver> device;
    @Mock
    private DeviceProvider<WebDriver> mockProvider;

    @Before
    public void setUp() {
        registry = new SpringDeviceRegistry(mockBeanFactory);
        device = directDevice("iphone", mockWebDriver);
    }

    @Test(expected = DeviceNotProvidedException.class)
    public void provideShouldRaiseDeviceNotProvidedExceptionWhenProviderNotRegistered() {
        given(mockBeanFactory.getBean("iphone", DeviceProvider.class))
                .willThrow(new NoSuchBeanDefinitionException("iphone"));

        registry.provide("iphone");
    }

    @Test
    public void provideShouldReturnDeviceFromProvider() {
        given(mockBeanFactory.getBean("iphone", DeviceProvider.class))
                .willReturn(mockProvider);

        given(mockProvider.get())
                .willReturn(device);

        assertThat(registry.provide("iphone"))
                .isSameAs(device);
    }

    @Test(expected = DeviceNotProvidedException.class)
    public void releaseShouldRaiseDeviceNotProvidedExceptionWhenProviderNotRegistered() {
        given(mockBeanFactory.getBean("iphone", DeviceProvider.class))
                .willThrow(new NoSuchBeanDefinitionException("iphone"));

        registry.release(device);
    }

    @Test
    public void releaseShouldReturnDeviceToProvider() {
        given(mockBeanFactory.getBean("iphone", DeviceProvider.class))
                .willReturn(mockProvider);

        registry.release(device);

        verify(mockProvider)
                .accept(device);
    }
}
