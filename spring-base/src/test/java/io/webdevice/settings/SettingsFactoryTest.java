package io.webdevice.settings;

import io.webdevice.test.Executor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

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
    public void shouldLoadFromDefaultBinder()
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
    public void shouldLoadFromPreferredBinder()
            throws Exception {
        Settings expected = new Settings()
                .withBaseUrl(new URL("http://mocked.io"))
                .withDefaultDevice("Mock Device")
                .withScope("mock-scope")
                .withEager(true)
                .withStrict(false);

        new Executor()
                .withClassesIn(new ClassPathResource("stubs/spring-boot-binder.jar"),
                        new ClassPathResource("stubs/configuration-properties-binder.jar"))
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
}
