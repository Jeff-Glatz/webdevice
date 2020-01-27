package io.webdevice.settings;

import io.webdevice.device.StubDeviceProvider;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class BasicDeviceDefinitionTest {
    private DeviceDefinition definition;

    @Before
    public void setUp() {
        definition = new DeviceDefinition();
    }

    @Test
    public void shouldReturnModifiableAliases() {
        definition.withAlias("Device");

        assertThat(definition.getAliases())
                .hasSize(1);

        definition.getAliases()
                .add("Local Device");

        assertThat(definition.getAliases())
                .hasSize(2);
    }

    @Test
    public void shouldSetAliases() {
        definition.setAliases(asList("Local Device", "Device"));

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

    @Test
    public void shouldReturnModifiableCapabilities() {
        definition.withCapability("name", "value");

        assertThat(definition.getCapabilities())
                .hasSize(1);

        definition.getCapabilities()
                .put("name-2", "value-2");

        assertThat(definition.getCapabilities())
                .hasSize(2);
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

    @Test
    public void shouldReturnModifiableExtraOptions() {
        definition.withExtraOption("name", "value");

        assertThat(definition.getExtraOptions())
                .hasSize(1);

        definition.getExtraOptions()
                .put("name-2", "value-2");

        assertThat(definition.getExtraOptions())
                .hasSize(2);
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

    @Test
    public void shouldReturnModifiableConfidentialKeys() {
        definition.withConfidential("accessKey");

        assertThat(definition.getConfidential())
                .hasSize(1);

        definition.getConfidential()
                .add("password");

        assertThat(definition.getConfidential())
                .hasSize(2);
    }

    @Test
    public void shouldSetConfidentialKeys() {
        definition.setConfidential(asList("accessKey", "password"));

        assertThat(definition.getConfidential())
                .containsExactly("accessKey", "password");
    }

    @Test
    public void twoProvidedDevicesShouldBeEqual() {
        definition.withName("myDevice")
                .withProvider(StubDeviceProvider.class);

        DeviceDefinition metadata2 = new DeviceDefinition()
                .withName("myDevice")
                .withProvider(StubDeviceProvider.class);

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
