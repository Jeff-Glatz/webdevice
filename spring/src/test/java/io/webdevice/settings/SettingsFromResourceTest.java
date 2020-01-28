package io.webdevice.settings;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.context.ApplicationContextException;

import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

public class SettingsFromResourceTest
        extends EnvironmentBasedTest {
    private SettingsFromResource function;

    @Before
    public void setUp() {
        function = new SettingsFromResource(environment);
    }

    @Test
    public void shouldLoadSettingsFromJsonResource()
            throws Exception {
        Settings expected = new Settings()
                .withDefaultDevice("Direct")
                .withBaseUrl(new URL("https://webdevice.io"))
                .withDevice(new DeviceDefinition()
                        .withName("Direct")
                        .withAlias("Local Direct")
                        .withDriver(FirefoxDriver.class)
                        .withCapability("version", "59")
                        .withPooled(true))
                .withDevice(new DeviceDefinition()
                        .withName("Remote")
                        .withAlias("iPhone")
                        .withRemoteAddress(new URL("http://selenium.grid:4444/wd/hub"))
                        .withCapability("version", "60")
                        .withCapability("username", "saucy")
                        .withCapability("accessKey", "2secret4u")
                        .withConfidential("accessKey")
                        .withPooled(false));

        Settings actual = function.apply("io/webdevice/wiring/direct-pooled-device.json");

        assertThat(actual)
                .isEqualTo(expected);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseIllegalArgumentExceptionFromJsonResourceWhenDriverClassNotFound()
            throws Exception {
        function.apply("io/webdevice/settings/driver-class-not-found.json");
    }

    @Test
    public void shouldLoadSettingsFromPropertiesResource()
            throws Exception {
        Settings expected = new Settings()
                .withBaseUrl(new URL("https://webdevice.io"))
                .withDefaultDevice("Remote")
                .withDevice(new DeviceDefinition()
                        .withName("Remote")
                        .withRemoteAddress(new URL("https://ondemand.saucelabs.com:443/wd/hub"))
                        .withCapability("username", "saucy")
                        .withCapability("accessKey", "2secret4u"));

        Settings actual = function.apply("io/webdevice/settings/device-with-placeholders.properties");
        assertThat(actual)
                .isEqualTo(expected);
    }

    @Test(expected = ApplicationContextException.class)
    public void shouldRaiseApplicationContextExceptionWhenResourceUnsupported()
            throws Exception {
        function.apply("io/webdevice/settings/unsupported-resource.yaml");
    }
}
