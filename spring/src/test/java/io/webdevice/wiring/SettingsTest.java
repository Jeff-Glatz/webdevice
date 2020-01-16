package io.webdevice.wiring;

import org.junit.Before;
import org.junit.Test;

import java.net.URL;

import static io.webdevice.wiring.Settings.settings;
import static org.assertj.core.api.Assertions.assertThat;

public class SettingsTest
        extends EnvironmentBasedTest {

    private Settings settings;

    @Before
    public void setUp() {
        settings = new Settings();
    }

    @Test
    public void shouldBindWebDeviceSpecificSettingsFromEnvironmentUsingDefaults()
            throws Exception {
        // Need to specify one value or binding will raise an exception
        settings.withDefaultDevice("Foo")
                .withStrict(true)
                .withEager(false)
                .withBaseUrl(null);

        Settings actual = settings(environmentWith("io/webdevice/wiring/default-device-only.yaml"));
        assertThat(actual)
                .isEqualTo(settings);
    }

    @Test
    public void shouldBindWebDeviceSpecificSettingsFromEnvironmentUsingNonDefaults()
            throws Exception {
        settings.withDefaultDevice("Foo")
                .withStrict(false)
                .withEager(true)
                .withBaseUrl(new URL("http://webdevice.io"));

        Settings actual = settings(environmentWith("io/webdevice/wiring/non-defaults.yaml"));
        assertThat(actual)
                .isEqualTo(settings);
    }

    @Test
    public void shouldStreamDevices() {
        DeviceSettings iPhone = new DeviceSettings()
                .withName("iPhone");
        DeviceSettings iPad = new DeviceSettings()
                .withName("iPad");

        settings.withDevice(iPhone)
                .withDevice(iPad);

        assertThat(settings.devices())
                .contains(iPhone, iPad);
    }
}
