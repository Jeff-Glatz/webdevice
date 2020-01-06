package io.webdevice.device;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.webdevice.test.UnitTest;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.MutableCapabilities;

import java.util.function.Function;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.openqa.selenium.remote.DesiredCapabilities.iphone;

public class DirectDeviceProviderTest
        extends UnitTest {
    @Mock
    private Appender mockAppender;
    @Captor
    private ArgumentCaptor<LoggingEvent> loggingEventCaptor;
    @Mock
    private Function<Class<WebDriverForTest>, WebDriverManager> mockFactory;
    private DirectDeviceProvider<WebDriverForTest> provider;

    @Mock
    private WebDriverManager mockManager;

    @Before
    public void setUp() {
        Logger.getLogger(DirectDeviceProvider.class)
                .addAppender(mockAppender);
        provider = new DirectDeviceProvider<>("iphone", WebDriverForTest.class, mockFactory);
    }

    @After
    public void tearDown() {
        Logger.getLogger(DirectDeviceProvider.class)
                .removeAppender(mockAppender);
    }

    @Test
    public void initializeShouldSetUpWebDriverManagerForType() {
        given(mockFactory.apply(WebDriverForTest.class))
                .willReturn(mockManager);

        provider.initialize();

        verify(mockManager)
                .setup();
        verify(mockFactory)
                .apply(WebDriverForTest.class);
        verifyNoMoreInteractions(mockFactory, mockManager);
    }

    @Test
    public void getShouldCreateDirectProviderUsingTypeConstructorWithNoArgs() {
        given(mockFactory.apply(WebDriverForTest.class))
                .willReturn(mockManager);

        provider.initialize();

        assertThat(provider.getCapabilities())
                .isNull();

        Device<WebDriverForTest> device = provider.get();

        assertThat(device.getDriver())
                .isInstanceOf(WebDriverForTest.class);
        assertThat(device.getName())
                .isEqualTo("iphone");
        assertThat(device.getSessionId())
                .isNotNull();
        // Multiple invocations should return same session id
        assertThat(device.getSessionId())
                .isSameAs(device.getSessionId());
    }

    @Test
    public void getShouldCreateDirectProviderUsingTypeConstructorWithCapabilities() {
        given(mockFactory.apply(WebDriverForTest.class))
                .willReturn(mockManager);

        provider.setCapabilities(iphone());
        provider.initialize();

        Device<WebDriverForTest> device = provider.get();

        assertThat(device.getDriver())
                .isInstanceOf(WebDriverForTest.class);
        assertThat(device.getName())
                .isEqualTo("iphone");
        assertThat(device.getSessionId())
                .isNotNull();
        // Multiple invocations should return same session id
        assertThat(device.getSessionId())
                .isSameAs(device.getSessionId());
        // Captured capabilities should be the same
        assertThat(device.as(HasCapabilities.class).getCapabilities())
                .isSameAs(provider.getCapabilities());
    }

    @Test
    public void getShouldProtectCapabilitiesWhenLogging() {
        given(mockFactory.apply(WebDriverForTest.class))
                .willReturn(mockManager);

        MutableCapabilities capabilities = new MutableCapabilities();
        capabilities.setCapability("accessKey", "2secret4u");

        provider.setCapabilities(capabilities);
        provider.setConfidential(singleton("accessKey"));
        provider.initialize();

        provider.get();

        verify(mockAppender, times(3))
                .doAppend(loggingEventCaptor.capture());
        assertThat(loggingEventCaptor.getValue().getMessage())
                .isEqualTo("Instantiating class io.webdevice.device.WebDriverForTest with capabilities {accessKey: ********}");
    }

    @Test
    public void disposeShouldDoNothing() {
        provider.dispose();

        verifyNoMoreInteractions(mockManager, mockFactory);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getConfidentialShouldReturnImmutableSet() {
        provider.getConfidential()
                .add("foo");
    }
}
