package io.webdevice.support;

import io.webdevice.device.Device;
import io.webdevice.device.DeviceNotProvidedException;
import io.webdevice.device.DeviceProvider;
import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openqa.selenium.WebDriver;

import static io.webdevice.device.Devices.directDevice;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class SimpleDeviceRegistryTest
        extends UnitTest {
    private SimpleDeviceRegistry registry;
    @Mock
    private WebDriver mockWebDriver;
    private Device<WebDriver> device;
    @Mock
    private DeviceProvider<WebDriver> mockProvider;

    @Before
    public void setUp() {
        registry = new SimpleDeviceRegistry();
        device = directDevice("iphone", mockWebDriver);
    }

    @Test(expected = DeviceNotProvidedException.class)
    public void provideShouldRaiseDeviceNotProvidedExceptionWhenProviderNotRegistered() {
        registry.provide("iphone");
    }

    @Test
    public void provideShouldReturnDeviceFromProvider() {
        registry.withProvider("iphone", mockProvider);

        given(mockProvider.get())
                .willReturn(device);

        assertThat(registry.provide("iphone"))
                .isSameAs(device);
    }

    @Test(expected = DeviceNotProvidedException.class)
    public void releaseShouldRaiseDeviceNotProvidedExceptionWhenProviderNotRegistered() {
        registry.release(device);
    }

    @Test
    public void releaseShouldReturnDeviceToProvider() {
        registry.withProvider("iphone", mockProvider)
                .release(device);

        verify(mockProvider)
                .accept(device);
    }
}
