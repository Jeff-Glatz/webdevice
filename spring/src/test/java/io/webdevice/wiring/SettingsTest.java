package io.webdevice.wiring;

import io.cucumber.spring.CucumberTestContext;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.FilteredClassLoader;

import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

import static io.webdevice.wiring.Settings.settings;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.beans.factory.support.AbstractBeanDefinition.SCOPE_DEFAULT;

public class SettingsTest
        extends EnvironmentBasedTest {

    private Settings settings;

    @Before
    public void setUp() {
        settings = new Settings();
    }

    @Test
    public void shouldBindWebDeviceSpecificSettingsFromEnvironmentUsingDefaults()
            throws Exception {
        // Need to specify one value or binding will raise an exception
        settings.withDefaultDevice("Foo")
                .withScope(null)
                .withStrict(true)
                .withEager(false)
                .withBaseUrl(null);

        Settings actual = settings(environmentWith("io/webdevice/wiring/default-device-only.yaml"));
        assertThat(actual)
                .isEqualTo(settings);
    }

    @Test
    public void shouldBindWebDeviceSpecificSettingsFromEnvironmentUsingNonDefaults()
            throws Exception {
        settings.withDefaultDevice("Foo")
                .withScope("prototype")
                .withStrict(false)
                .withEager(true)
                .withBaseUrl(new URL("http://webdevice.io"));

        Settings actual = settings(environmentWith("io/webdevice/wiring/non-defaults.yaml"));
        assertThat(actual)
                .isEqualTo(settings);
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
    public void shouldReturnDefaultScopeWhenNotSpecifiedAndCucumberNotPresent()
            throws Exception {
        AtomicReference<String> scope = new AtomicReference<>(null);
        Thread thread = new Thread(() -> {
            scope.set(new Settings()
                    .withScope(null)
                    .getScope());
        });
        thread.setContextClassLoader(new FilteredClassLoader(CucumberTestContext.class));
        thread.start();
        thread.join();

        assertThat(scope.get())
                .isEqualTo(SCOPE_DEFAULT);
    }

    @Test
    public void shouldReturnCucumberScopeWhenNotSpecifiedAncCucumberPresent() {
        settings.withScope(null);

        assertThat(settings.getScope())
                .isEqualTo("cucumber-glue");
    }

    @Test
    public void shouldReturnScopeWhenSpecified() {
        settings.withScope("application");

        assertThat(settings.getScope())
                .isEqualTo("application");
    }
}
