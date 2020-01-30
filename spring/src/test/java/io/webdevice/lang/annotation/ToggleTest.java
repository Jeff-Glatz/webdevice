package io.webdevice.lang.annotation;

import org.junit.Test;

import static io.webdevice.lang.annotation.Toggle.OFF;
import static io.webdevice.lang.annotation.Toggle.ON;
import static io.webdevice.lang.annotation.Toggle.UNSET;
import static org.assertj.core.api.Assertions.assertThat;

public class ToggleTest {

    @Test
    public void shouldRepresentOnState() {
        Toggle toggle = ON;

        assertThat(toggle.on())
                .isTrue();
        assertThat(toggle.off())
                .isFalse();
        assertThat(toggle.unset())
                .isFalse();
        assertThat(toggle.valueOf())
                .isTrue();
        assertThat(toggle.toString())
                .isEqualTo("true");
    }

    @Test
    public void shouldRepresentOffState() {
        Toggle toggle = OFF;

        assertThat(toggle.on())
                .isFalse();
        assertThat(toggle.off())
                .isTrue();
        assertThat(toggle.unset())
                .isFalse();
        assertThat(toggle.valueOf())
                .isFalse();
        assertThat(toggle.toString())
                .isEqualTo("false");
    }

    @Test
    public void shouldRepresentUnsetState() {
        Toggle toggle = UNSET;

        assertThat(toggle.on())
                .isFalse();
        assertThat(toggle.off())
                .isFalse();
        assertThat(toggle.unset())
                .isTrue();
        assertThat(toggle.valueOf())
                .isNull();
        assertThat(toggle.toString())
                .isNull();
    }
}
