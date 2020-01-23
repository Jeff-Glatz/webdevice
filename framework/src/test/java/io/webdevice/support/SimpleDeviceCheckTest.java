package io.webdevice.support;

import io.webdevice.device.Device;
import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import static io.webdevice.device.Devices.fixedSession;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class SimpleDeviceCheckTest
        extends UnitTest {
    private SimpleDeviceCheck<WebDriver> check;

    @Mock
    private WebDriver mockWebDriver;
    private Device<WebDriver> device;

    @Before
    public void setUp() {
        check = new SimpleDeviceCheck<>();
        device = new Device<>("iphone", mockWebDriver, fixedSession());
    }

    @Test
    public void shouldReturnTrueWhenCurrentUrlIsObtained() {
        given(mockWebDriver.getCurrentUrl())
                .willReturn("foo");

        assertThat(check.test(device))
                .isTrue();
    }

    @Test
    public void shouldReturnFalseWhenWebDriverExceptionIsRaised() {
        given(mockWebDriver.getCurrentUrl())
                .willThrow(new WebDriverException());

        assertThat(check.test(device))
                .isFalse();
    }
}
