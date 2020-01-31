package io.webdevice.settings;

import io.bestquality.util.Sandbox;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContextException;

import java.net.URL;

import static io.bestquality.util.MapBuilder.mapOf;
import static io.webdevice.wiring.WebDeviceScope.namespace;
import static org.assertj.core.api.Assertions.assertThat;

public class SettingsFactoryTest
        extends EnvironmentBasedTest {
    private SettingsFactory factory;

    @Before
    public void setUp() {
        factory = new SettingsFactory(environment);
    }

    @After
    public void tearDown() {
        MockSettingsBinder.uninstall();
    }

    @Test
    public void shouldLoadFromDefaultBinderWhenSpringBootNotPresentAndNoCustomBinderSpecified()
            throws
            Exception {
        Settings expected = new Settings()
                .withStrict(false)
                .withEager(true)
                .withScope("custom")
                .withDefaultDevice("Custom")
                .withBaseUrl(new URL("https://www.webdevice.io"));

        environmentWith(mapOf(String.class, Object.class)
                .with("webdevice.baseUrl", expected.getBaseUrl())
                .with("webdevice.defaultDevice", expected.getDefaultDevice())
                .with("webdevice.scope", expected.getScope())
                .with("webdevice.eager", expected.isEager())
                .with("webdevice.strict", expected.isStrict())
                .build());

        Settings actual = factory.from(environment);
        assertThat(actual)
                .isEqualTo(expected);
    }

    @Test
    public void shouldLoadFromDefaultBinderWhenSpringBootPresentButConfigurationPropertiesBinderMissing()
            throws Throwable {
        Settings expected = new Settings()
                .withStrict(false)
                .withEager(true)
                .withScope("custom")
                .withDefaultDevice("Custom")
                .withBaseUrl(new URL("https://www.webdevice.io"));

        environmentWith(mapOf(String.class, Object.class)
                .with("webdevice.baseUrl", expected.getBaseUrl())
                .with("webdevice.defaultDevice", expected.getDefaultDevice())
                .with("webdevice.scope", expected.getScope())
                .with("webdevice.eager", expected.isEager())
                .with("webdevice.strict", expected.isStrict())
                .build());

        new Sandbox()
                .withClassesIn("stubs/spring-boot-binder.jar")
                .execute(() -> {
                    SettingsFactory factory = new SettingsFactory(environment);
                    Settings actual = factory.from(environment);
                    assertThat(actual)
                            .isEqualTo(expected);
                });
    }

    @Test
    public void shouldLoadFromConfigurationPropertiesBinder()
            throws Throwable {
        Settings expected = new Settings()
                .withBaseUrl(new URL("http://mocked.io"))
                .withDefaultDevice("Mock Device")
                .withScope("mock-scope")
                .withEager(true)
                .withStrict(false);

        new Sandbox()
                .withClassesIn("stubs/spring-boot-binder.jar",
                        "stubs/configuration-properties-binder.jar")
                .execute(() -> {
                    SettingsFactory factory = new SettingsFactory(environment);
                    Settings actual = factory.from(environment);
                    assertThat(actual)
                            .isEqualTo(expected);
                });
    }

    @Test
    public void shouldLoadFromCustomizedBinder() {
        Settings expected = MockSettingsBinder.install(new Settings());

        environmentWith(mapOf(String.class, Object.class)
                .with(namespace("binder"), MockSettingsBinder.class.getName())
                .build());

        Settings actual = factory.from(environment);
        assertThat(actual)
                .isSameAs(expected);
    }

    @Test(expected = ApplicationContextException.class)
    public void shouldPropagateExceptionInstantiatingCustomBinderByWrapping() {
        environmentWith(mapOf(String.class, Object.class)
                .with(namespace("binder"), BadSettingsBinder.class.getName())
                .build());

        factory.from(environment);
    }
}
