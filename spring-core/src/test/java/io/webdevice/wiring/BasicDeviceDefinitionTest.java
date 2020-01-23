package io.webdevice.wiring;

import io.webdevice.support.GenericDeviceProvider;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.HashMap;
import java.util.Map;

import static io.webdevice.util.Collections.setOf;
import static org.assertj.core.api.Assertions.assertThat;

public class BasicDeviceDefinitionTest {
    private DeviceDefinition definition;

    @Before
    public void setUp() {
        definition = new DeviceDefinition();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldReturnUnmodifiableAliases() {
        definition.withAlias("Device");

        definition.getAliases()
                .add("Local Device");
    }

    @Test
    public void shouldSetAliases() {
        definition.setAliases(setOf("Local Device", "Device"));

        assertThat(definition.getAliases())
                .containsExactly("Local Device", "Device");
    }

    @Test
    public void shouldReturnStreamOfAliases() {
        definition.withAlias("Device")
                .withAlias("Local Device");

        assertThat(definition.aliases())
                .containsExactly("Device", "Local Device");
    }

    @Test
    public void shouldSetPooled() {
        // Should not be pooled by default
        assertThat(definition.isPooled())
                .isFalse();

        definition.withPooled(true);

        assertThat(definition.isPooled())
                .isTrue();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldReturnUnmodifiableCapabilities() {
        definition.withCapability("name", "value");

        definition.getCapabilities()
                .put("name-2", "value-2");
    }

    @Test
    public void shouldSetCapabilities() {
        Map<String, Object> capabilities = new HashMap<>();
        capabilities.put("name-1", "value-1");
        capabilities.put("name-2", "value-2");

        definition.setCapabilities(capabilities);

        assertThat(definition.getCapabilities())
                .isEqualTo(capabilities);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldReturnUnmodifiableExtraOptions() {
        definition.withExtraOption("name", "value");

        definition.getExtraOptions()
                .put("name-2", "value-2");
    }

    @Test
    public void shouldSetExtraOptions() {
        Map<String, Object> extraOptions = new HashMap<>();
        extraOptions.put("name-1", "value-1");
        extraOptions.put("name-2", "value-2");

        definition.setExtraOptions(extraOptions);

        assertThat(definition.getExtraOptions())
                .isEqualTo(extraOptions);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldReturnUnmodifiableConfidentialKeys() {
        definition.withConfidential("accessKey");

        definition.getConfidential()
                .add("password");
    }

    @Test
    public void shouldSetConfidentialKeys() {
        definition.setConfidential(setOf("accessKey", "password"));

        assertThat(definition.getConfidential())
                .containsExactly("accessKey", "password");
    }

    @Test
    public void twoProvidedDevicesShouldBeEqual() {
        definition.withName("myDevice")
                .withProvider(GenericDeviceProvider.class);

        DeviceDefinition metadata2 = new DeviceDefinition()
                .withName("myDevice")
                .withProvider(GenericDeviceProvider.class);

        assertThat(metadata2)
                .isEqualTo(definition);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseExceptionWhenDesiredCapabilitiesFactoryMethodDoesNotExist() {
        definition.withName("myDevice")
                .withDriver(RemoteWebDriver.class)
                .withDesired("doesNotExist")
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseExceptionWhenFailureOccursConstructingOptions() {
        definition.withName("myDevice")
                .withDriver(RemoteWebDriver.class)
                .withOptions(BadOptions.class)
                .build();
    }

    public static class BadOptions
            extends MutableCapabilities {
        private BadOptions() {
        }
    }
}
