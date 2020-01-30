package io.webdevice.settings;

import org.junit.Test;

import static io.webdevice.settings.SettingsHelper.normalize;
import static org.assertj.core.api.Assertions.assertThat;

public class SettingsHelperTest {

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
}
