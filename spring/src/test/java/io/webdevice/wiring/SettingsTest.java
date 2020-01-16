package io.webdevice.wiring;

import io.webdevice.support.YamlPropertySourceFactory;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.net.URL;

import static io.webdevice.wiring.Settings.settings;
import static org.assertj.core.api.Assertions.assertThat;

public class SettingsTest {

    private PropertySourceFactory propertySourceFactory;
    private StandardEnvironment environment;
    private Settings settings;

    @Before
    public void setUp() {
        propertySourceFactory = new YamlPropertySourceFactory();
        environment = new StandardEnvironment();
        environment.setConversionService(new ApplicationConversionService());
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

        Settings actual = settings(environmentFrom("io/webdevice/wiring/default-device-only.yaml"));
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

        Settings actual = settings(environmentFrom("io/webdevice/wiring/non-defaults.yaml"));
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

    private StandardEnvironment environmentFrom(String resource)
            throws IOException {
        environment.getPropertySources()
                .addFirst(propertySourceFactory
                        .createPropertySource("webdevice",
                                new EncodedResource(new ClassPathResource(resource))));
        return environment;
    }
}
