package io.webdevice.device;

import io.webdevice.test.UnitTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.ImmutableCapabilities;
import org.openqa.selenium.MutableCapabilities;

import java.net.URL;

import static java.lang.String.format;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.openqa.selenium.remote.DesiredCapabilities.iphone;

public class RemoteDeviceProviderTest
        extends UnitTest {
    @Mock
    private Appender mockAppender;
    @Captor
    private ArgumentCaptor<LoggingEvent> loggingEventCaptor;
    private URL remoteAddress;
    private MockWebServer mockWebServer;
    private RemoteDeviceProvider provider;

    @Before
    public void setUp()
            throws Exception {
        Logger.getLogger(RemoteDeviceProvider.class)
                .addAppender(mockAppender);
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        remoteAddress = mockWebServer.url("")
                .url();
        provider = new RemoteDeviceProvider("iphone", remoteAddress);
    }

    @After
    public void tearDown() {
        Logger.getLogger(RemoteDeviceProvider.class)
                .removeAppender(mockAppender);
    }

    @Test
    public void shouldTakeNameAndRemoteAddressFromConstructor() {
        assertThat(provider.getName())
                .isEqualTo("iphone");
        assertThat(provider.getRemoteAddress())
                .isEqualTo(remoteAddress);
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
    public void getShouldProtectCapabilitiesWhenLogging() {
        MutableCapabilities capabilities = new MutableCapabilities();
        capabilities.setCapability("accessKey", "2secret4u");

        provider.setCapabilities(capabilities);
        provider.setConfidential(singleton("accessKey"));
        provider.initialize();

        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"status\": 0, \"sessionId\": \"aa\", \"value\": {}}"));

        provider.get();

        verify(mockAppender)
                .doAppend(loggingEventCaptor.capture());
        assertThat(loggingEventCaptor.getValue().getMessage())
                .isEqualTo(format("Providing new device named iphone connecting to %s with capabilities {accessKey: ********}", remoteAddress));
    }

    @Test
    public void disposeShouldDoNothing() {
        provider.dispose();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getConfidentialShouldReturnImmutableSet() {
        provider.getConfidential()
                .add("foo");
    }
}
