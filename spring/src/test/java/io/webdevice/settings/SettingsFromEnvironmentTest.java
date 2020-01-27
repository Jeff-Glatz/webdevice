package io.webdevice.settings;

import io.webdevice.support.AnnotationAttributes;
import io.webdevice.support.AnnotationAttributes.ConversionFailedException;
import io.webdevice.wiring.EnableWebDevice;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static io.bestquality.util.MapBuilder.newMap;
import static org.assertj.core.api.Assertions.assertThat;

public class SettingsFromEnvironmentTest
        extends EnvironmentBasedTest {
    private Map<String, Object> annotationAttributes;
    private SettingsFromEnvironment supplier;

    @Before
    public void setUp() {
        annotationAttributes = new LinkedHashMap<>();
        supplier = new SettingsFromEnvironment(environment,
                new AnnotationAttributes(EnableWebDevice.class, annotationAttributes));
    }

    @After
    public void tearDown() {
        MockSettingsBinder.uninstall();
    }

    @Test(expected = ConversionFailedException.class)
    public void shouldRaiseExceptionWhenDefaultValueSupplierBinderClassNotFound()
            throws Exception {
        // Setup the default unknown value supplier
        environmentWith(newMap(String.class, Object.class)
                .with("webdevice.binder", "io.webdevice.settings.UnknownBinder")
                .build());

        // Same as default value of EnableWebDevice.binder to use the default value supplier
        annotationAttributes.put("binder", SettingsBinder.class);

        supplier.get();
    }

    @Test
    public void shouldFallbackToDefaultValueSupplierUsingBinderFromEnvironmentPropertyWhenBinderNotCustomized()
            throws Exception {
        Settings expected = MockSettingsBinder.install(new Settings());

        // Setup the default value supplier
        environmentWith(newMap(String.class, Object.class)
                .with("webdevice.binder", MockSettingsBinder.class.getName())
                .build());

        // Same as default value of EnableWebDevice.binder to use the default value supplier
        annotationAttributes.put("binder", SettingsBinder.class);

        assertThat(supplier.get())
                .isSameAs(expected);
    }

    @Test
    public void shouldFallbackToDefaultValueSupplierUsingDefaultSettingsBinderWhenBinderNotCustomizedAndBinderNotInEnvironment()
            throws Exception {
        // Same as default value of EnableWebDevice.binder to use the default value supplier
        annotationAttributes.put("binder", SettingsBinder.class);

        // Should create an empty Settings object (no values are present)
        assertThat(supplier.get())
                .isEqualTo(new Settings());
    }

    @Test
    public void shouldFallbackToDefaultValueSupplierUsingDefaultSettingsBinderWhenSpringBootBinderPresentButConfigurationPropertiesBinderIsNot()
            throws Exception {
        AtomicReference<Settings> actual = new AtomicReference<>();

        // SpringBoot binder is available, but the ConfigurationPropertiesBinder is not
        Thread executor = new Thread(() -> {
            try {
                actual.set(supplier.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        // Setup a custom classloader that allows org.springframework.boot.context.properties.bind.Binder to be seen
        executor.setContextClassLoader(new URLClassLoader(new URL[]{
                new ClassPathResource("stubs/spring-boot-binder.jar")
                        .getURL()},
                getClass().getClassLoader()));
        executor.setUncaughtExceptionHandler((t, e) -> {
            throw (RuntimeException) e;
        });
        executor.start();
        executor.join();

        // Should create an empty Settings object (no values are present)
        assertThat(actual.get())
                .isEqualTo(new Settings());
    }

    @Test
    public void shouldFallbackToDefaultValueSupplierUsingConfigurationPropertiesBinderWhenPresent()
            throws Exception {
        // The stub settings binder uses these values to create the settings
        environmentWith(newMap(String.class, Object.class)
                .with("webdevice.baseUrl", "https://foo.com")
                .with("webdevice.defaultDevice", "Foo")
                .with("webdevice.scope", "prototype")
                .with("webdevice.eager", "true")
                .with("webdevice.strict", "false")
                .build());

        AtomicReference<Settings> actual = new AtomicReference<>();
        Thread executor = new Thread(() -> {
            try {
                actual.set(supplier.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        executor.setContextClassLoader(new URLClassLoader(new URL[]{
                new ClassPathResource("stubs/spring-boot-binder.jar")
                        .getURL(),
                new ClassPathResource("stubs/configuration-properties-binder.jar")
                        .getURL()},
                getClass().getClassLoader()));
        executor.setUncaughtExceptionHandler((t, e) -> {
            throw (RuntimeException) e;
        });
        executor.start();
        executor.join();

        assertThat(actual.get())
                .isEqualTo(new Settings()
                        .withBaseUrl(new URL("https://foo.com"))
                        .withDefaultDevice("Foo")
                        .withScope("prototype")
                        .withEager(true)
                        .withStrict(false));
    }
}
