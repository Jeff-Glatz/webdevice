package io.webdevice.settings;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.atomic.AtomicReference;

import static io.webdevice.net.MaskingClassLoader.classLoaderMasking;
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
        Thread executor = new Thread(() -> scope.set(
                new Settings()
                        .withScope(null)
                        .getScope()));
        // Setup a custom classloader that prevents CucumberTestContext from being seen
        executor.setContextClassLoader(classLoaderMasking("io.cucumber.spring.CucumberTestContext"));
        executor.start();
        executor.join();

        assertThat(scope.get())
                .isEqualTo("webdevice");
    }

    @Test
    public void shouldReturnCucumberGlueScopeWhenNotSpecifiedAndCucumberPresent()
            throws Exception {
        AtomicReference<String> scope = new AtomicReference<>(null);
        Thread executor = new Thread(() -> scope.set(
                new Settings()
                        .withScope(null)
                        .getScope()));
        // Setup a custom classloader that allows CucumberTestContext to be seen
        executor.setContextClassLoader(new URLClassLoader(new URL[]{
                new ClassPathResource("stubs/cucumber-stub.jar")
                        .getURL()},
                getClass().getClassLoader()));
        executor.start();
        executor.join();

        assertThat(scope.get())
                .isEqualTo("cucumber-glue");
    }

    @Test
    public void shouldReturnScopeWhenSpecified() {
        settings.withScope("singleton");

        assertThat(settings.getScope())
                .isEqualTo("singleton");
    }
}
