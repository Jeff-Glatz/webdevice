package io.webdevice.wiring;

import io.webdevice.device.Device;
import io.webdevice.device.DeviceRegistry;
import io.webdevice.device.WebDevice;
import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openqa.selenium.WebDriver;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class WebDeviceRuntimeTest
        extends UnitTest {
    private Settings settings;
    private WebDeviceRuntime runtime;

    @Mock
    private DeviceRegistry mockRegistry;
    @Mock
    private Device<WebDriver> mockDevice;

    @Before
    public void setUp() {
        settings = new Settings();
        runtime = new WebDeviceRuntime(settings);
    }

    @Test
    public void shouldCreateWebDeviceFromSettings()
            throws Exception {
        settings.withBaseUrl(new URL("http://localhost"))
                .withDefaultDevice("iphone")
                .withEager(true)
                .withStrict(true);

        WebDevice webDevice = runtime.webDevice(mockRegistry);

        assertThat(webDevice.getBaseUrl())
                .isEqualTo(new URL("http://localhost"));
        assertThat(webDevice.getDefaultDevice())
                .isEqualTo("iphone");
        assertThat(webDevice.isEager())
                .isTrue();
        assertThat(webDevice.isStrict())
                .isTrue();

        given(mockRegistry.provide("iphone"))
                .willReturn(mockDevice);

        webDevice.initialize();
    }
}
