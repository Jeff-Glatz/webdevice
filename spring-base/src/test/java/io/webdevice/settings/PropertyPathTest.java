package io.webdevice.settings;

import org.junit.Test;

import static io.webdevice.settings.PropertyPath.normalize;
import static org.assertj.core.api.Assertions.assertThat;

public class PropertyPathTest {

    @Test
    public void shouldConvertDashesToCamelCase() {
        assertThat(normalize("one"))
                .isEqualTo("one");
        assertThat(normalize("One"))
                .isEqualTo("One");
        assertThat(normalize("oneTwo"))
                .isEqualTo("oneTwo");
        assertThat(normalize("OneTwo"))
                .isEqualTo("OneTwo");

        assertThat(normalize("one-two"))
                .isEqualTo("oneTwo");
        assertThat(normalize("one-two-three"))
                .isEqualTo("oneTwoThree");
    }

    @Test
    public void shouldConvertDevices() {
        assertThat(normalize("devices[Direct].pooled"))
                .isEqualTo("devices[Direct].pooled");

        assertThat(normalize("devices.Direct.pooled"))
                .isEqualTo("devices[Direct].pooled");
    }

    @Test
    public void shouldConvertCapabilities() {
        assertThat(normalize("devices[Direct].capabilities[username]"))
                .isEqualTo("devices[Direct].capabilities[username]");

        assertThat(normalize("devices.Direct.capabilities.username"))
                .isEqualTo("devices[Direct].capabilities[username]");
        assertThat(normalize("devices[Direct].capabilities.username"))
                .isEqualTo("devices[Direct].capabilities[username]");
    }

    @Test
    public void shouldConvertExtraOptions() {
        assertThat(normalize("devices[Direct].extra-options[username]"))
                .isEqualTo("devices[Direct].extraOptions[username]");
        assertThat(normalize("devices[Direct].extraOptions[username]"))
                .isEqualTo("devices[Direct].extraOptions[username]");

        assertThat(normalize("devices.Direct.extra-options.username"))
                .isEqualTo("devices[Direct].extraOptions[username]");
        assertThat(normalize("devices.Direct.extraOptions.username"))
                .isEqualTo("devices[Direct].extraOptions[username]");

        assertThat(normalize("devices[Direct].extra-options.username"))
                .isEqualTo("devices[Direct].extraOptions[username]");
        assertThat(normalize("devices[Direct].extraOptions.username"))
                .isEqualTo("devices[Direct].extraOptions[username]");
    }

    @Test
    public void shouldNotDisKebabMapKeys() {
        assertThat(normalize("one[two-three]"))
                .isEqualTo("one[two-three]");
        assertThat(normalize("one-two[three-four]"))
                .isEqualTo("oneTwo[three-four]");
        assertThat(normalize("one-two-two-two[three-four]"))
                .isEqualTo("oneTwoTwoTwo[three-four]");

        assertThat(normalize("devices[Direct-Device].capabilities[user-name]"))
                .isEqualTo("devices[Direct-Device].capabilities[user-name]");
        assertThat(normalize("device-list[Direct-Device].capabilities-map[user-name]"))
                .isEqualTo("deviceList[Direct-Device].capabilitiesMap[user-name]");
        assertThat(normalize("long-device-list[Direct-Device].big-capabilities-map[user-name]"))
                .isEqualTo("longDeviceList[Direct-Device].bigCapabilitiesMap[user-name]");
    }

    @Test
    public void shouldTokenize() {

    }
}
