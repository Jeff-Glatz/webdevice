package io.webdevice.device;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DeviceNotProvidedExceptionTest {

    @Test
    public void shouldReturnDeviceName() {
        assertThat(new DeviceNotProvidedException("Firefox").getDevice())
                .isEqualTo("Firefox");
    }

    @Test
    public void shouldProperlyFormatMessage() {
        assertThat(new DeviceNotProvidedException("Firefox").getMessage())
                .isEqualTo("The device named Firefox is not being provided");

        assertThat(new DeviceNotProvidedException("Firefox", new Exception()).getMessage())
                .isEqualTo("The device named Firefox is not being provided");
    }
}
