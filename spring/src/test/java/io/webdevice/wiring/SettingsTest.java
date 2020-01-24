package io.webdevice.wiring;

import io.cucumber.spring.CucumberTestContext;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static io.webdevice.net.MaskingClassLoader.classLoaderMasking;
import static io.webdevice.wiring.Settings.scope;
import static org.assertj.core.api.Assertions.assertThat;

public class SettingsTest {

    private Settings settings;

    @Before
    public void setUp() {
        settings = new Settings();
    }

    @Test
    public void shouldStreamDevices() {
        DeviceDefinition iPhone = new DeviceDefinition()
                .withName("iPhone");
        DeviceDefinition iPad = new DeviceDefinition()
                .withName("iPad");

        settings.withDevice(iPhone)
                .withDevice(iPad);

        assertThat(settings.devices())
                .contains(iPhone, iPad);
    }

    @Test
    public void shouldReturnWebDeviceScopeWhenNotSpecifiedAndCucumberNotPresent()
            throws Exception {
        AtomicReference<String> scope = new AtomicReference<>(null);
        Thread thread = new Thread(() -> scope.set(
                new Settings()
                        .withScope(null)
                        .getScope()));
        // Setup a custom classloader that prevents CucumberTestContext from being seen
        thread.setContextClassLoader(classLoaderMasking(CucumberTestContext.class));
        thread.start();
        thread.join();

        assertThat(scope.get())
                .isEqualTo("webdevice");
    }

    @Test
    public void shouldReturnCucumberScopeWhenNotSpecifiedAncCucumberPresent() {
        settings.withScope(null);

        assertThat(settings.getScope())
                .isEqualTo("cucumber-glue");
    }

    @Test
    public void shouldReturnScopeWhenSpecified() {
        settings.withScope("singleton");

        assertThat(settings.getScope())
                .isEqualTo("singleton");
    }

    @Test
    public void settingsShouldReturnWebDeviceScopeWhenCucumberNotPresent()
            throws Exception {
        AtomicReference<String> scope = new AtomicReference<>(null);
        Thread thread = new Thread(() -> scope.set(scope()));
        // Setup a custom classloader that prevents CucumberTestContext from being seen
        thread.setContextClassLoader(classLoaderMasking(CucumberTestContext.class));
        thread.start();
        thread.join();

        assertThat(scope.get())
                .isEqualTo("webdevice");
    }

    @Test
    public void settingsShouldReturnCucumberScopeWhenPresent() {
        assertThat(scope())
                .isEqualTo("cucumber-glue");
    }
}
