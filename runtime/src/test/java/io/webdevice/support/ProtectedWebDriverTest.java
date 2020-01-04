package io.webdevice.support;

import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.Command;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.Response;

import java.util.LinkedHashSet;
import java.util.Set;

import static com.google.common.collect.ImmutableMap.of;
import static io.webdevice.device.Devices.randomSessionId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.openqa.selenium.Platform.MAC;
import static org.openqa.selenium.remote.BrowserType.IPAD;
import static org.openqa.selenium.remote.CapabilityType.BROWSER_NAME;
import static org.openqa.selenium.remote.CapabilityType.PLATFORM_NAME;
import static org.openqa.selenium.remote.DesiredCapabilities.iphone;
import static org.openqa.selenium.remote.DriverCommand.NEW_SESSION;

public class ProtectedWebDriverTest
        extends UnitTest {
    private Set<String> confidential;
    private Capabilities desiredCapabilities;
    @Mock
    private CommandExecutor mockCommandExecutor;

    @Before
    public void setUp() {
        desiredCapabilities = iphone();
        confidential = new LinkedHashSet<>();
    }

    @Test
    public void shouldProtectCapabilitiesAfterConstruction()
            throws Exception {
        given(mockCommandExecutor.execute(newSessionCommand()))
                .willReturn(iPadOnMacResponse());

        ProtectedWebDriver driver = newDriver();

        assertThat(driver.getCapabilities())
                .isInstanceOf(ProtectedCapabilities.class);
    }

    @Test
    public void shouldProtectCapabilitiesAfterConstructionAndDriverUse()
            throws Exception {
        given(mockCommandExecutor.execute(newSessionCommand()))
                .willReturn(iPadOnMacResponse());

        ProtectedWebDriver driver = newDriver();

        driver.get("foo");

        assertThat(driver.getCapabilities())
                .isInstanceOf(ProtectedCapabilities.class);
    }

    private Command newSessionCommand() {
        return new Command(null, NEW_SESSION, of("desiredCapabilities", desiredCapabilities));
    }

    private Response iPadOnMacResponse() {
        Response response = new Response(randomSessionId());
        response.setValue(of(BROWSER_NAME, IPAD, PLATFORM_NAME, MAC.toString()));
        return response;
    }

    private ProtectedWebDriver newDriver() {
        return new ProtectedWebDriver(mockCommandExecutor, desiredCapabilities, confidential);
    }
}
