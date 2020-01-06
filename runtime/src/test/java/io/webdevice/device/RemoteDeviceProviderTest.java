package io.webdevice.device;

import io.webdevice.test.UnitTest;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.ImmutableCapabilities;
import org.openqa.selenium.MutableCapabilities;

import java.net.HttpURLConnection;
import java.net.URL;

import static engineering.bestquality.protocol.mock.MockConnectionRegistry.clearMockConnections;
import static engineering.bestquality.protocol.mock.MockConnectionRegistry.registerMockConnection;
import static java.lang.System.clearProperty;
import static java.lang.System.setProperty;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.openqa.selenium.remote.DesiredCapabilities.iphone;

public class RemoteDeviceProviderTest
        extends UnitTest {
    @Mock
    private Appender mockAppender;
    @Captor
    private ArgumentCaptor<LoggingEvent> loggingEventCaptor;
    @Mock
    private HttpURLConnection mockURLConnection;
    private RemoteDeviceProvider provider;

    @BeforeClass
    public static void registerProtocolHandler() {
        setProperty("java.protocol.handler.pkgs", "engineering.bestquality.protocol");
    }

    @AfterClass
    public static void deregisterProtocolHandler() {
        clearProperty("java.protocol.handler.pkgs");
    }

    @Before
    public void setUp()
            throws Exception {
        Logger.getLogger(RemoteDeviceProvider.class)
                .addAppender(mockAppender);
        registerMockConnection(mockURLConnection);
        provider = new RemoteDeviceProvider("iphone", new URL("mock://localhost"));
    }

    @After
    public void tearDown() {
        clearMockConnections();
        Logger.getLogger(RemoteDeviceProvider.class)
                .removeAppender(mockAppender);
    }

    @Test
    public void initializeShouldSetImmutableCapabilitiesIfNoneProvided() {
        assertThat(provider.getCapabilities())
                .isNull();

        provider.initialize();

        assertThat(provider.getCapabilities())
                .isInstanceOf(ImmutableCapabilities.class);
    }

    @Test
    public void initializeShouldNotSetImmutableCapabilitiesIfProvided() {
        Capabilities capabilities = iphone();

        provider.setCapabilities(capabilities);

        provider.initialize();

        assertThat(provider.getCapabilities())
                .isSameAs(capabilities);
    }

    @Test
    @Ignore
    public void getShouldProtectCapabilitiesWhenLogging() {
        MutableCapabilities capabilities = new MutableCapabilities();
        capabilities.setCapability("accessKey", "2secret4u");

        provider.setCapabilities(capabilities);
        provider.setConfidential(singleton("accessKey"));
        provider.initialize();

        provider.get();

        verify(mockAppender, times(3))
                .doAppend(loggingEventCaptor.capture());
        assertThat(loggingEventCaptor.getValue().getMessage())
                .isEqualTo("Providing new device named iphone connecting to mock://localhost with capabilities {accessKey: ********}");
    }

    @Test
    public void disposeShouldDoNothing() {
        provider.dispose();

        verifyNoMoreInteractions(mockURLConnection);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getConfidentialShouldReturnImmutableSet() {
        provider.getConfidential()
                .add("foo");
    }
}
