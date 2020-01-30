package io.webdevice.wiring;

import io.webdevice.settings.EnvironmentBasedTest;
import io.webdevice.test.Executor;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContextException;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.yaml.snakeyaml.Yaml;

import static io.bestquality.util.MapBuilder.newMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class WebDeviceSettingsTest
        extends EnvironmentBasedTest {
    @Mock
    private AnnotationMetadata mockMetadata;
    @Mock
    private BeanDefinitionRegistry mockRegistry;

    private WebDeviceSettings exporter;

    @Before
    public void setUp() {
        exporter = new WebDeviceSettings(
                environmentWith(newMap(String.class, Object.class)
                        .with("saucelabs_username", "saucy")
                        .with("saucelabs_accessKey", "2secret4u")
                        .build()),
                new DefaultResourceLoader());
    }

    @Test
    public void shouldExportSettingsFromYamlResource() {
        given(mockMetadata.getAnnotationAttributes(EnableWebDevice.class.getName()))
                .willReturn(newMap(String.class, Object.class)
                        .with("settings", "io/webdevice/wiring/direct-and-remote-devices.yaml")
                        .build());

        exporter.registerBeanDefinitions(mockMetadata, mockRegistry);

        assertThat(environment.getProperty("webdevice.default-device"))
                .isEqualTo("Direct");
        assertThat(environment.getProperty("webdevice.base-url"))
                .isEqualTo("https://webdevice.io");

        assertThat(environment.getProperty("webdevice.devices.Direct.aliases"))
                .isEqualTo("Local Direct, Firefox");
        assertThat(environment.getProperty("webdevice.devices.Direct.driver"))
                .isEqualTo("org.openqa.selenium.firefox.FirefoxDriver");
        assertThat(environment.getProperty("webdevice.devices.Direct.pooled"))
                .isEqualTo("true");
        assertThat(environment.getProperty("webdevice.devices.Direct.capabilities.version"))
                .isEqualTo("59");

        assertThat(environment.getProperty("webdevice.devices.Remote.aliases[0]"))
                .isEqualTo("iPhone");
        assertThat(environment.getProperty("webdevice.devices.Remote.aliases[1]"))
                .isEqualTo("iPhone 8");
        assertThat(environment.getProperty("webdevice.devices.Remote.remoteAddress"))
                .isEqualTo("http://selenium.grid:4444/wd/hub");
        assertThat(environment.getProperty("webdevice.devices.Remote.pooled"))
                .isEqualTo("false");
        assertThat(environment.getProperty("webdevice.devices.Remote.capabilities.version"))
                .isEqualTo("60");
        assertThat(environment.getProperty("webdevice.devices.Remote.capabilities.username"))
                .isEqualTo("saucy");
        assertThat(environment.getProperty("webdevice.devices.Remote.capabilities.accessKey"))
                .isEqualTo("2secret4u");
        assertThat(environment.getProperty("webdevice.devices.Remote.confidential[0]"))
                .isEqualTo("accessKey");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldRaiseIllegalStateExceptionFromYamlResourceWhenYamlClassNotFound()
            throws Exception {
        given(mockMetadata.getAnnotationAttributes(EnableWebDevice.class.getName()))
                .willReturn(newMap(String.class, Object.class)
                        .with("settings", "io/webdevice/wiring/driver-class-not-found.yaml")
                        .build());

        new Executor()
                .withMaskedClasses(Yaml.class)
                .execute(() -> {
                    exporter.registerBeanDefinitions(mockMetadata, mockRegistry);
                });
    }

    @Test
    public void shouldLoadSettingsFromPropertiesResource() {
        given(mockMetadata.getAnnotationAttributes(EnableWebDevice.class.getName()))
                .willReturn(newMap(String.class, Object.class)
                        .with("settings", "io/webdevice/wiring/device-with-placeholders.properties")
                        .build());

        exporter.registerBeanDefinitions(mockMetadata, mockRegistry);

        assertThat(environment.getProperty("webdevice.base-url"))
                .isEqualTo("https://webdevice.io");
        assertThat(environment.getProperty("webdevice.defaultDevice"))
                .isEqualTo("Remote");
        assertThat(environment.getProperty("webdevice.scope"))
                .isEqualTo("singleton");
        assertThat(environment.getProperty("webdevice.eager"))
                .isEqualTo("true");
        assertThat(environment.getProperty("webdevice.strict"))
                .isEqualTo("false");

        assertThat(environment.getProperty("webdevice.devices[Remote].remote-address"))
                .isEqualTo("https://ondemand.saucelabs.com:443/wd/hub");
        assertThat(environment.getProperty("webdevice.devices[Remote].capabilities[username]"))
                .isEqualTo("saucy");
        assertThat(environment.getProperty("webdevice.devices[Remote].capabilities[accessKey]"))
                .isEqualTo("2secret4u");
    }

    @Test(expected = ApplicationContextException.class)
    public void shouldRaiseApplicationContextExceptionWhenResourceUnsupported() {
        given(mockMetadata.getAnnotationAttributes(EnableWebDevice.class.getName()))
                .willReturn(newMap(String.class, Object.class)
                        .with("settings", "io/webdevice/wiring/unsupported-resource.foo")
                        .build());

        exporter.registerBeanDefinitions(mockMetadata, mockRegistry);
    }

    @Test
    public void annotationPropertySourceShouldTakePrecendenceOverSettings() {
        given(mockMetadata.getAnnotationAttributes(EnableWebDevice.class.getName()))
                .willReturn(newMap(String.class, Object.class)
                        .with("settings", "io/webdevice/wiring/device-with-placeholders.properties")
                        .with("baseUrl", "http://www.webdevice.io")
                        .with("defaultDevice", "Direct")
                        .with("scope", "application")
                        .with("eager", "false")
                        .with("strict", "true")
                        .build());

        exporter.registerBeanDefinitions(mockMetadata, mockRegistry);

        // TODO: Address this
        assertThat(environment.getProperty("webdevice.base-url"))
                .isEqualTo("https://webdevice.io");
        assertThat(environment.getProperty("webdevice.baseUrl"))
                .isEqualTo("http://www.webdevice.io");

        assertThat(environment.getProperty("webdevice.defaultDevice"))
                .isEqualTo("Direct");
        assertThat(environment.getProperty("webdevice.scope"))
                .isEqualTo("application");
        assertThat(environment.getProperty("webdevice.eager"))
                .isEqualTo("false");
        assertThat(environment.getProperty("webdevice.strict"))
                .isEqualTo("true");

        assertThat(environment.getProperty("webdevice.devices[Remote].remote-address"))
                .isEqualTo("https://ondemand.saucelabs.com:443/wd/hub");
        assertThat(environment.getProperty("webdevice.devices[Remote].capabilities[username]"))
                .isEqualTo("saucy");
        assertThat(environment.getProperty("webdevice.devices[Remote].capabilities[accessKey]"))
                .isEqualTo("2secret4u");
    }
}
