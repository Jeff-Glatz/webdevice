package io.webdevice.device;

import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Interactive;

import java.net.URL;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.webdevice.device.Devices.direct;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

public class WebDeviceTest
        extends UnitTest {
    @Mock
    private DeviceRegistry mockDeviceRegistry;
    @Mock(extraInterfaces = {
            JavascriptExecutor.class, HasCapabilities.class,
            Interactive.class, TakesScreenshot.class
    })
    private WebDriver mockWebDriver;
    private Device<WebDriver> device;
    private Device<WebDriver> device2;
    private WebDevice webDevice;
    @Mock
    private Consumer<WebDriver> mockConsumer;
    // This function can return anything, for the test this will behave like perform()
    @Mock
    private Function<WebDriver, WebDevice> mockFunction;
    @Mock
    private WebDriver.Navigation mockNavigation;

    @Before
    public void setUp()
            throws Exception {
        device = direct("iphone", mockWebDriver);
        device2 = direct("ipad", mockWebDriver);
        webDevice = new WebDevice(mockDeviceRegistry)
                .withBaseUrl(new URL("http://localhost"));
    }

    @Test
    public void shouldCanonicalizeUrls()
            throws Exception {
        assertThat(webDevice.absolute("http://remotehost"))
                .isEqualTo("http://remotehost");

        assertThat(webDevice.absolute("foo"))
                .isEqualTo("http://localhost/foo");
        assertThat(webDevice.absolute("/foo"))
                .isEqualTo("http://localhost/foo");

        webDevice.withBaseUrl(new URL("http://localhost/"));

        assertThat(webDevice.absolute("foo"))
                .isEqualTo("http://localhost/foo");
        assertThat(webDevice.absolute("/foo"))
                .isEqualTo("http://localhost/foo");
    }

    @Test
    public void initializeShouldAcquireDefaultDevice() {
        given(mockDeviceRegistry.provide("iphone"))
                .willReturn(device);

        webDevice.withDefaultDevice("iphone")
                .withEager(true)
                .initialize();

        assertThat(webDevice.device())
                .isSameAs(device);
    }

    @Test
    public void initializeShouldNotAcquireDefaultDevice() {
        webDevice.withDefaultDevice("iphone")
                .withEager(false)
                .initialize();

        assertThat(webDevice.acquired())
                .isFalse();
    }

    @Test
    public void useShouldRaiseExceptionWhenStrictAndDeviceAlreadyAcquired() {
        given(mockDeviceRegistry.provide("iphone"))
                .willReturn(device);

        webDevice.withDefaultDevice("iphone")
                .withEager(true)
                .withStrict(true)
                .initialize();

        assertThat(webDevice.acquired())
                .isTrue();

        try {
            webDevice.use("iphone");
            fail("Expected an exception");
        } catch (IllegalStateException e) {
            assertThat(e).
                    hasMessage("Browser has already been acquired for the current scenario");
        }
    }

    @Test
    public void useShouldReleaseDeviceWhenNotStrictAndDeviceAlreadyAcquired() {
        given(mockDeviceRegistry.provide("iphone"))
                .willReturn(device);
        given(mockDeviceRegistry.provide("ipad"))
                .willReturn(device2);

        webDevice.withDefaultDevice("iphone")
                .withEager(true)
                .withStrict(false)
                .initialize();

        assertThat(webDevice.device())
                .isSameAs(device);

        WebDevice fluent = webDevice.use("ipad");

        assertThat(fluent)
                .isSameAs(webDevice);
        assertThat(webDevice.device())
                .isSameAs(device2);

        verify(mockDeviceRegistry)
                .release(device);
    }

    @Test
    public void useDefaultShouldAcquireDefaultDevice() {
        given(mockDeviceRegistry.provide("iphone"))
                .willReturn(device);

        webDevice.withDefaultDevice("iphone")
                .withEager(false)
                .withStrict(true)
                .initialize();

        assertThat(webDevice.acquired())
                .isFalse();

        WebDevice fluent = webDevice.useDefault();

        assertThat(fluent)
                .isSameAs(webDevice);
        assertThat(webDevice.device())
                .isSameAs(device);
    }

    @Test
    public void homeShouldNavigateToBaseUrl()
            throws Exception {
        given(mockWebDriver.navigate())
                .willReturn(mockNavigation);
        given(mockDeviceRegistry.provide("iphone"))
                .willReturn(device);

        webDevice.withDefaultDevice("iphone")
                .withEager(true)
                .withStrict(true)
                .initialize();

        WebDevice fluent = webDevice.home();

        assertThat(fluent)
                .isSameAs(webDevice);
        verify(mockNavigation)
                .to(new URL("http://localhost"));
    }

    @Test
    public void performShouldExecuteWithDriver() {
        given(mockDeviceRegistry.provide("iphone"))
                .willReturn(device);

        webDevice.withDefaultDevice("iphone")
                .withEager(true)
                .withStrict(true)
                .initialize();

        WebDevice fluent = webDevice.perform(mockConsumer);

        assertThat(fluent)
                .isSameAs(webDevice);
        verify(mockConsumer)
                .accept(mockWebDriver);
    }

    @Test
    public void invokeShouldExecuteWithDriver() {
        given(mockDeviceRegistry.provide("iphone"))
                .willReturn(device);
        given(mockFunction.apply(mockWebDriver))
                .willReturn(webDevice);

        webDevice.withDefaultDevice("iphone")
                .withEager(true)
                .withStrict(true)
                .initialize();

        WebDevice fluent = webDevice.invoke(mockFunction);

        assertThat(fluent)
                .isSameAs(webDevice);
        verify(mockFunction)
                .apply(mockWebDriver);
    }

    @Test
    public void releaseShouldNotFailWhenDeviceHasNotBeenAcquired() {
        assertThat(webDevice.acquired())
                .isFalse();

        webDevice.release();

        assertThat(webDevice.acquired())
                .isFalse();
    }

    @Test
    public void releaseShouldReleaseDeviceAndClearReferenceWhenOneHasBeenAcquired() {
        given(mockDeviceRegistry.provide("iphone"))
                .willReturn(device);

        webDevice.withDefaultDevice("iphone")
                .withEager(true)
                .initialize();

        assertThat(webDevice.acquired())
                .isTrue();

        webDevice.release();

        assertThat(webDevice.acquired())
                .isFalse();

        verify(mockDeviceRegistry)
                .release(device);
    }

    @Test
    public void releaseShouldClearDeviceReferenceWhenExceptionIsRaisedReleasingToRegistry() {
        given(mockDeviceRegistry.provide("iphone"))
                .willReturn(device);

        webDevice.withDefaultDevice("iphone")
                .withEager(true)
                .initialize();

        assertThat(webDevice.acquired())
                .isTrue();

        willThrow(new DeviceNotProvidedException("iphone"))
                .given(mockDeviceRegistry)
                .release(device);

        try {
            webDevice.release();
            fail("Expected an exception");
        } catch (DeviceNotProvidedException e) {
        }

        assertThat(webDevice.acquired())
                .isFalse();

        verify(mockDeviceRegistry)
                .release(device);
    }
}
