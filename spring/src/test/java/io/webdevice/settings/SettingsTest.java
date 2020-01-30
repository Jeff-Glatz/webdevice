package io.webdevice.settings;

import io.webdevice.test.Executor;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.util.concurrent.atomic.AtomicReference;

import static io.bestquality.util.MapBuilder.newMap;
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
    public void shouldApplyDeviceNameFromKeyWhenSetAsMap() {
        settings.setDevices(newMap(String.class, DeviceDefinition.class)
                .with("Firefox", new DeviceDefinition())
                .with("iPhone8", new DeviceDefinition())
                .build());

        assertThat(settings.device("Firefox").getName())
                .isEqualTo("Firefox");
        assertThat(settings.device("iPhone8").getName())
                .isEqualTo("iPhone8");
    }

    @Test
    public void shouldApplyDeviceNameFromKeyWhenPutDirectlyInMap() {
        DeviceDefinition device = new DeviceDefinition()
                .withName("Foo");

        settings.getDevices()
                .put("Bar", device);

        assertThat(device.getName())
                .isEqualTo("Bar");
        assertThat(settings.device("Bar"))
                .isSameAs(device);
    }

    @Test
    public void shouldFindDeviceByKeyWhenDirectlyAddedToDeviceMapAndKeyIsPresent() {
        DeviceDefinition device = new DeviceDefinition();

        settings.getDevices()
                .put("iPhone8", device);

        assertThat(settings.device("iPhone8"))
                .isSameAs(device);
    }

    @Test
    public void shouldReturnWebDeviceScopeWhenNotSpecifiedAndCucumberNotPresent()
            throws Exception {
        // Setup a custom classloader that prevents CucumberTestContext from being seen
        AtomicReference<String> scope = new AtomicReference<>(null);
        new Executor()
                .withMaskedClasses("io.cucumber.spring.CucumberTestContext")
                .execute(() -> scope.set(
                        new Settings()
                                .withScope(null)
                                .getScope()));

        assertThat(scope.get())
                .isEqualTo("webdevice");
    }

    @Test
    public void shouldReturnCucumberGlueScopeWhenNotSpecifiedAndCucumberPresent()
            throws Exception {
        // Setup a custom classloader that allows CucumberTestContext to be seen
        AtomicReference<String> scope = new AtomicReference<>(null);
        new Executor()
                .withClassesIn(new ClassPathResource("stubs/cucumber-stub.jar"))
                .execute(() -> scope.set(
                        new Settings()
                                .withScope(null)
                                .getScope()));

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
