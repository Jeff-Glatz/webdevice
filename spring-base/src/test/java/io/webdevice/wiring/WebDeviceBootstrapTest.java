package io.webdevice.wiring;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class WebDeviceBootstrapTest {
    private WebDeviceBootstrap bootstrap;

    @Before
    public void setUp() {
        bootstrap = new WebDeviceBootstrap();
    }

    @Test
    public void shouldReturnTwoRegistrars() {
        assertThat(bootstrap.selectImports(null))
                .containsExactly(
                        SettingsExporter.class.getName(),
                        WebDeviceRuntime.class.getName());
    }
}
