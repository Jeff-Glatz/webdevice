package io.webdevice.device;

import io.webdevice.test.UnitTest;
import org.junit.Test;
import org.mockito.Mock;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import java.util.function.Function;

import static io.webdevice.device.Devices.directDevice;
import static io.webdevice.device.Devices.directProvider;
import static io.webdevice.device.Devices.fixedSession;
import static io.webdevice.device.Devices.provider;
import static io.webdevice.device.Devices.randomSessionId;
import static io.webdevice.device.Devices.remoteDevice;
import static io.webdevice.device.Devices.remoteProvider;
import static io.webdevice.device.Devices.remoteSession;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class DevicesTest
        extends UnitTest {
    @Mock
    private WebDriver mockWebDriver;

    @Mock
    private RemoteWebDriver mockRemoteWebDriver;

    @Test
    public void randomSessionIdShouldReturnNewSessionIdEachTime() {
        assertThat(randomSessionId())
                .isNotEqualTo(randomSessionId());
    }

    @Test
    public void fixedSessionFunctionWithSessionIdShouldReturnSameSessionId() {
        SessionId sessionId = randomSessionId();
        Function<WebDriver, SessionId> function = fixedSession(sessionId);

        assertThat(function.apply(null))
                .isSameAs(sessionId);
        assertThat(function.apply(mockWebDriver))
                .isSameAs(sessionId);
    }

    @Test
    public void fixedSessionFunctionShouldReturnSameSessionId() {
        Function<WebDriver, SessionId> function = fixedSession();
        SessionId sessionId = function.apply(null);

        assertThat(function.apply(mockWebDriver))
                .isSameAs(sessionId);
        assertThat(function.apply(mockRemoteWebDriver))
                .isSameAs(sessionId);
    }

    @Test
    public void remoteSessionFunctionShouldReturnSameSessionIdFromRemoteWebDriver() {
        SessionId sessionId = randomSessionId();
        given(mockRemoteWebDriver.getSessionId())
                .willReturn(sessionId);

        Function<RemoteWebDriver, SessionId> function = remoteSession();

        assertThat(function.apply(mockRemoteWebDriver))
                .isSameAs(sessionId);
        assertThat(function.apply(mockRemoteWebDriver))
                .isSameAs(sessionId);
    }

    @Test
    public void directDeviceShouldCreateDeviceUsingGeneratedSessionId() {
        Device<WebDriver> device = directDevice("iphone", mockWebDriver);

        assertThat(device.getName())
                .isEqualTo("iphone");
        assertThat(device.getDriver())
                .isSameAs(mockWebDriver);

        SessionId sessionId = device.getSessionId();
        assertThat(sessionId)
                .isNotNull();
        assertThat(device.getSessionId())
                .isSameAs(sessionId);
    }

    @Test
    public void remoteDeviceShouldCreateDeviceUsingSessionIdFromDriver() {
        SessionId sessionId = randomSessionId();
        given(mockRemoteWebDriver.getSessionId())
                .willReturn(sessionId);

        Device<RemoteWebDriver> device = remoteDevice("iphone", mockRemoteWebDriver);

        assertThat(device.getName())
                .isEqualTo("iphone");
        assertThat(device.getDriver())
                .isSameAs(mockRemoteWebDriver);
        assertThat(device.getSessionId())
                .isSameAs(sessionId);
        assertThat(device.getSessionId())
                .isSameAs(sessionId);
    }

    @Test
    public void providerShouldCreateDevicesUsingDeviceSupplier() {
        SessionId sessionId = randomSessionId();
        given(mockRemoteWebDriver.getSessionId())
                .willReturn(sessionId);

        Device<RemoteWebDriver> singleton = remoteDevice("iphone", mockRemoteWebDriver);

        // For testing purposes, this provider will always return the same device
        DeviceProvider<RemoteWebDriver> provider = provider(() -> singleton);

        Device<RemoteWebDriver> device = provider.get();
        assertThat(device)
                .isSameAs(singleton);
        assertThat(device.getName())
                .isEqualTo("iphone");
        assertThat(device.getDriver())
                .isSameAs(mockRemoteWebDriver);
        assertThat(device.getSessionId())
                .isSameAs(sessionId);
        assertThat(device.getSessionId())
                .isSameAs(sessionId);

        provider.accept(device);
        verify(mockRemoteWebDriver)
                .quit();
    }

    @Test
    public void providerShouldCreateDevicesUsingNameDriverSupplierAndSessionFunction() {
        SessionId sessionId = randomSessionId();
        given(mockRemoteWebDriver.getSessionId())
                .willReturn(sessionId);

        // For testing purposes, this provider will always return the same device
        DeviceProvider<RemoteWebDriver> provider = provider("iphone", () -> mockRemoteWebDriver, remoteSession());

        Device<RemoteWebDriver> device = provider.get();
        assertThat(device.getName())
                .isEqualTo("iphone");
        assertThat(device.getDriver())
                .isSameAs(mockRemoteWebDriver);
        assertThat(device.getSessionId())
                .isSameAs(sessionId);
        assertThat(device.getSessionId())
                .isSameAs(sessionId);

        provider.accept(device);
        verify(mockRemoteWebDriver)
                .quit();
    }

    @Test
    public void directProviderShouldCreateDevicesUsingNameDriverSupplier() {

        // For testing purposes, this provider will always return the same device
        DeviceProvider<WebDriver> provider = directProvider("iphone", () -> mockWebDriver);

        Device<WebDriver> device = provider.get();
        assertThat(device.getName())
                .isEqualTo("iphone");
        assertThat(device.getDriver())
                .isSameAs(mockWebDriver);

        SessionId sessionId = device.getSessionId();
        assertThat(sessionId)
                .isNotNull();
        assertThat(device.getSessionId())
                .isSameAs(sessionId);

        provider.accept(device);
        verify(mockWebDriver)
                .quit();
    }

    @Test
    public void remoteProviderShouldCreateDevicesUsingNameDriverSupplier() {
        SessionId sessionId = randomSessionId();
        given(mockRemoteWebDriver.getSessionId())
                .willReturn(sessionId);

        // For testing purposes, this provider will always return the same device
        DeviceProvider<RemoteWebDriver> provider = remoteProvider("iphone", () -> mockRemoteWebDriver);

        Device<RemoteWebDriver> device = provider.get();
        assertThat(device.getName())
                .isEqualTo("iphone");
        assertThat(device.getDriver())
                .isSameAs(mockRemoteWebDriver);
        assertThat(device.getSessionId())
                .isSameAs(sessionId);
        assertThat(device.getSessionId())
                .isSameAs(sessionId);

        provider.accept(device);
        verify(mockRemoteWebDriver)
                .quit();
    }
}
