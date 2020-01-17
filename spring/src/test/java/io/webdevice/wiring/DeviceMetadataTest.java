package io.webdevice.wiring;

import io.webdevice.support.CustomFirefoxProvider;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.HashMap;
import java.util.Map;

import static io.webdevice.util.Collections.setOf;
import static org.assertj.core.api.Assertions.assertThat;

public class DeviceMetadataTest {
    private DeviceMetadata metadata;

    @Before
    public void setUp() {
        metadata = new DeviceMetadata();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldReturnUnmodifiableAliases() {
        metadata.withAlias("Device");

        metadata.getAliases()
                .add("Local Device");
    }

    @Test
    public void shouldSetAliases() {
        metadata.setAliases(setOf("Local Device", "Device"));

        assertThat(metadata.getAliases())
                .containsExactly("Local Device", "Device");
    }

    @Test
    public void shouldReturnStreamOfAliases() {
        metadata.withAlias("Device")
                .withAlias("Local Device");

        assertThat(metadata.aliases())
                .containsExactly("Device", "Local Device");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldReturnUnmodifiableCapabilities() {
        metadata.withCapability("name", "value");

        metadata.getCapabilities()
                .put("name-2", "value-2");
    }

    @Test
    public void shouldSetCapabilities() {
        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("name-1", "value-1");
        capabilities.put("name-2", "value-2");

        metadata.setCapabilities(capabilities);

        assertThat(metadata.getCapabilities())
                .isEqualTo(capabilities);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldReturnUnmodifiableExtraOptions() {
        metadata.withExtraOption("name", "value");

        metadata.getExtraOptions()
                .put("name-2", "value-2");
    }

    @Test
    public void shouldSetExtraOptions() {
        Map<String, Object> extraOptions = new HashMap<>();
        extraOptions.put("name-1", "value-1");
        extraOptions.put("name-2", "value-2");

        metadata.setExtraOptions(extraOptions);

        assertThat(metadata.getExtraOptions())
                .isEqualTo(extraOptions);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldReturnUnmodifiableConfidentialKeys() {
        metadata.withConfidential("accessKey");

        metadata.getConfidential()
                .add("password");
    }

    @Test
    public void shouldSetConfidentialKeys() {
        metadata.setConfidential(setOf("accessKey", "password"));

        assertThat(metadata.getConfidential())
                .containsExactly("accessKey", "password");
    }

    @Test
    public void twoProvidedDevicesShouldBeEqual() {
        metadata.withName("myDevice")
                .withProvider(CustomFirefoxProvider.class);

        DeviceMetadata metadata2 = new DeviceMetadata()
                .withName("myDevice")
                .withProvider(CustomFirefoxProvider.class);

        assertThat(metadata2)
                .isEqualTo(metadata);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseExceptionWhenDesiredCapabilitiesFactoryMethodDoesNotExist() {
        metadata.withName("myDevice")
                .withDriver(FirefoxDriver.class)
                .withDesired("doesNotExist")
                .buildDefinition();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseExceptionWhenFailureOccursConstructingOptions() {
        metadata.withName("myDevice")
                .withDriver(FirefoxDriver.class)
                .withOptions(BadOptions.class)
                .buildDefinition();
    }

    public static class BadOptions
            extends FirefoxOptions {
        private BadOptions() {
        }
    }
}
