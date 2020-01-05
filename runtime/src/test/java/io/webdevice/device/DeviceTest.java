package io.webdevice.device;

import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;

import java.util.function.Function;

import static io.webdevice.device.Devices.randomSessionId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

public class DeviceTest
        extends UnitTest {
    @Mock(extraInterfaces = JavascriptExecutor.class)
    private WebDriver mockWebDriver;
    @Mock
    private Function<WebDriver, SessionId> mockSessionFunction;

    private Device<WebDriver> device;
    private SessionId sessionId;

    @Before
    public void setUp() {
        device = new Device<>("iphone", mockWebDriver, mockSessionFunction);
        sessionId = randomSessionId();
    }

    @Test
    public void shouldReturnName() {
        assertThat(device.getName())
                .isEqualTo("iphone");
    }

    @Test
    public void shouldReturnDriver() {
        assertThat(device.getDriver())
                .isSameAs(mockWebDriver);
    }

    @Test
    public void asShouldCastToImplementedInterface() {
        JavascriptExecutor executor = device.as(JavascriptExecutor.class);

        assertThat(executor)
                .isSameAs(mockWebDriver);
    }

    @Test
    public void shouldUseFunctionToComputeSessionId() {
        given(mockSessionFunction.apply(mockWebDriver))
                .willReturn(sessionId);

        assertThat(device.getSessionId())
                .isSameAs(sessionId);
    }

    @Test
    public void shouldPerformActionWithDriver() {
        device.perform((driver) -> {
            assertThat(driver)
                    .isSameAs(mockWebDriver);
            driver.quit();
        });

        verify(mockWebDriver)
                .quit();
    }

    @Test
    public void shouldInvokeFunctionWithDriver() {
        SessionId actual = device.invoke((driver) -> {
            assertThat(driver)
                    .isSameAs(mockWebDriver);
            return sessionId;
        });

        assertThat(actual)
                .isSameAs(sessionId);
    }
}
