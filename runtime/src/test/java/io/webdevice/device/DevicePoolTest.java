package io.webdevice.device;

import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Function;

import static io.webdevice.device.Devices.randomSessionId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class DevicePoolTest
        extends UnitTest {
    @Mock
    private Device<WebDriver> mockDevice;
    @Mock
    private Device<WebDriver> mockDevice2;
    @Mock
    private DeviceProvider<WebDriver> mockProvider;
    @Mock
    private Function<Device<WebDriver>, Boolean> mockTestFunction;

    private BlockingDeque<Device<WebDriver>> free;
    private BlockingDeque<Device<WebDriver>> used;
    private DevicePool<WebDriver> pool;

    private SessionId sessionId;

    @Before
    public void setUp() {
        free = new LinkedBlockingDeque<>();
        used = new LinkedBlockingDeque<>();
        pool = new DevicePool<>("device", mockProvider, mockTestFunction, free, used);

        sessionId = randomSessionId();
    }

    @Test
    public void getShouldCreateAndPoolDeviceWhenNoneAreFree() {
        assertThat(free.isEmpty())
                .isTrue();

        given(mockDevice.getSessionId())
                .willReturn(sessionId);
        given(mockProvider.get())
                .willReturn(mockDevice);

        Device<WebDriver> actual = pool.get();

        assertThat(actual)
                .isSameAs(mockDevice);
        assertThat(free)
                .isEmpty();
        assertThat(used)
                .contains(actual);

        verify(mockDevice, atLeastOnce())
                .getSessionId();
        verify(mockProvider)
                .get();

        verifyNoMoreInteractions(mockDevice, mockProvider);
    }

    @Test
    public void getShouldProvidePooledDeviceWhenUsableDeviceIsAcquiredFromFree() {
        free.add(mockDevice);

        given(mockTestFunction.apply(mockDevice))
                .willReturn(true);
        given(mockDevice.getSessionId())
                .willReturn(sessionId);

        Device<WebDriver> actual = pool.get();

        assertThat(actual)
                .isSameAs(mockDevice);
        assertThat(free)
                .isEmpty();
        assertThat(used)
                .contains(actual);

        verify(mockTestFunction, atLeastOnce())
                .apply(mockDevice);
        verify(mockDevice, atLeastOnce())
                .getSessionId();

        verifyNoMoreInteractions(mockDevice, mockProvider);
    }

    @Test
    public void getShouldReleaseDeviceToProviderAndCreateNewOneWhenUnusableDeviceIsAcquiredFromFree() {
        // Device 1 is in pool, but not usable
        free.add(mockDevice);
        given(mockTestFunction.apply(mockDevice))
                .willReturn(false);
        given(mockDevice.getSessionId())
                .willReturn(sessionId);

        // Setup the provider to create a new device
        given(mockDevice2.getSessionId())
                .willReturn(randomSessionId());
        given(mockProvider.get())
                .willReturn(mockDevice2);

        Device<WebDriver> actual = pool.get();

        // The new device should be in the used deque and none in the free
        assertThat(actual)
                .isSameAs(mockDevice2);
        assertThat(free)
                .isEmpty();
        assertThat(used)
                .contains(actual);

        verify(mockTestFunction, atLeastOnce())
                .apply(mockDevice);
        verify(mockDevice, atLeastOnce())
                .getSessionId();
        verify(mockDevice2, atLeastOnce())
                .getSessionId();

        // Verify the unusable device was returned to provider
        verify(mockProvider)
                .accept(mockDevice);
        // Verify the provider was asked to create a device once
        verify(mockProvider)
                .get();

        verifyNoMoreInteractions(mockDevice, mockDevice2, mockProvider);
    }

    @Test
    public void getShouldReleaseDeviceToProviderConsumingExceptionAndCreateNewOneWhenUnusableDeviceIsAcquiredFromFree() {
        // Device 1 is in pool, but not usable
        free.add(mockDevice);
        given(mockTestFunction.apply(mockDevice))
                .willReturn(false);
        given(mockDevice.getSessionId())
                .willReturn(sessionId);

        // Raise an exception when the device is released to provider
        willThrow(new RuntimeException("boom1"))
                .given(mockProvider)
                .accept(mockDevice);

        // Setup the provider to create a new device
        given(mockDevice2.getSessionId())
                .willReturn(randomSessionId());
        given(mockProvider.get())
                .willReturn(mockDevice2);

        Device<WebDriver> actual = pool.get();

        // The new device should be in the used deque and none in the free
        assertThat(actual)
                .isSameAs(mockDevice2);
        assertThat(free)
                .isEmpty();
        assertThat(used)
                .contains(actual);

        verify(mockTestFunction, atLeastOnce())
                .apply(mockDevice);
        verify(mockDevice, atLeastOnce())
                .getSessionId();
        verify(mockDevice2, atLeastOnce())
                .getSessionId();

        // Verify the unusable device was returned to provider
        verify(mockProvider)
                .accept(mockDevice);
        // Verify the provider was asked to create a device once
        verify(mockProvider)
                .get();

        verifyNoMoreInteractions(mockDevice, mockDevice2, mockProvider);
    }

    @Test
    public void acceptShouldOnlyTakeDeviceIfItWasUsed() {
        used.add(mockDevice);

        assertThat(used.isEmpty())
                .isFalse();
        assertThat(free.isEmpty())
                .isTrue();

        pool.accept(mockDevice);

        assertThat(used.isEmpty())
                .isTrue();
        assertThat(free.isEmpty())
                .isFalse();

        verify(mockDevice, atLeastOnce())
                .getSessionId();

        verifyNoMoreInteractions(mockDevice, mockDevice2, mockProvider);
    }

    @Test
    public void acceptShouldIgnoreDeviceIfItWasNotUsed() {
        assertThat(used.isEmpty())
                .isTrue();
        assertThat(free.isEmpty())
                .isTrue();

        pool.accept(mockDevice);

        assertThat(used.isEmpty())
                .isTrue();
        assertThat(free.isEmpty())
                .isTrue();

        verify(mockDevice, atLeastOnce())
                .getSessionId();

        verifyNoMoreInteractions(mockDevice, mockDevice2, mockProvider);
    }

    @Test
    public void disposeShouldReleaseAllDevicesToProvider() {
        used.add(mockDevice2);
        free.add(mockDevice);

        assertThat(used.isEmpty())
                .isFalse();
        assertThat(free.isEmpty())
                .isFalse();

        pool.dispose();

        assertThat(used.isEmpty())
                .isTrue();
        assertThat(free.isEmpty())
                .isTrue();

        verify(mockDevice, atLeastOnce())
                .getSessionId();
        verify(mockProvider)
                .accept(mockDevice);

        verify(mockDevice2, atLeastOnce())
                .getSessionId();
        verify(mockProvider)
                .accept(mockDevice2);

        verifyNoMoreInteractions(mockDevice, mockDevice2, mockProvider);
    }

    @Test
    public void disposeShouldReleaseAllDevicesToProviderConsumingAllExceptions() {
        used.add(mockDevice2);
        free.add(mockDevice);

        assertThat(used.isEmpty())
                .isFalse();
        assertThat(free.isEmpty())
                .isFalse();

        willThrow(new RuntimeException("boom1"))
                .given(mockProvider)
                .accept(mockDevice);
        willThrow(new RuntimeException("boom2"))
                .given(mockProvider)
                .accept(mockDevice2);

        pool.dispose();

        assertThat(used.isEmpty())
                .isTrue();
        assertThat(free.isEmpty())
                .isTrue();

        verify(mockDevice, atLeastOnce())
                .getSessionId();
        verify(mockProvider)
                .accept(mockDevice);

        verify(mockDevice2, atLeastOnce())
                .getSessionId();
        verify(mockProvider)
                .accept(mockDevice2);

        verifyNoMoreInteractions(mockDevice, mockDevice2, mockProvider);
    }
}
