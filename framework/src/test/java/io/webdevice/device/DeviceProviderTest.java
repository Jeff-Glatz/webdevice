package io.webdevice.device;

import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openqa.selenium.WebDriver;

import static io.webdevice.device.Devices.directDevice;
import static org.mockito.Mockito.verify;

public class DeviceProviderTest
        extends UnitTest {
    @Mock
    private WebDriver mockWebDriver;
    private DeviceProvider<WebDriver> provider;

    @Before
    public void setUp() {
        provider = () -> null;
    }

    @Test
    public void acceptShouldQuitWebDriver() {
        provider.accept(directDevice("iphone", mockWebDriver));

        verify(mockWebDriver)
                .quit();
    }
}
