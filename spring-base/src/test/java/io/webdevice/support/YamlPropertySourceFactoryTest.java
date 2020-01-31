package io.webdevice.support;

import io.bestquality.util.Sandbox;
import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.yaml.snakeyaml.Yaml;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static io.bestquality.net.MaskingClassLoader.maskingClasses;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class YamlPropertySourceFactoryTest
        extends UnitTest {
    @Mock
    private Resource mockResource;
    private EncodedResource encodedResource;
    private YamlPropertySourceFactory factory;

    @Before
    public void setUp() {
        encodedResource = new EncodedResource(
                new ClassPathResource("io/webdevice/wiring/direct-and-remote-devices.yaml"));
        factory = new YamlPropertySourceFactory();
    }

    @Test
    public void shouldUseSuppliedName()
            throws IOException {
        PropertySource<?> source = factory.createPropertySource("foo", encodedResource);

        assertThat(source.getName())
                .isEqualTo("foo");
    }

    @Test
    public void shouldUseResourceNameWhenSuppliedNameIsNull()
            throws IOException {
        PropertySource<?> source = factory.createPropertySource(null, encodedResource);

        assertThat(source.getName())
                .isEqualTo("direct-and-remote-devices.yaml");
    }

    @Test
    public void shouldResolvePropertyValue()
            throws IOException {
        PropertySource<?> source = factory.createPropertySource(null, encodedResource);

        assertThat(source.getProperty("webdevice.base-url"))
                .isEqualTo("https://webdevice.io");
    }

    @Test
    public void shouldDelegateWhenNotYamlFile()
            throws IOException {
        PropertySource<?> source = factory.createPropertySource(null,
                new EncodedResource(new ClassPathResource("io/webdevice/wiring/default-device-only.properties")));

        assertThat(source.getProperty("webdevice.default-device"))
                .isEqualTo("Foo");
    }

    @Test
    public void shouldDelegateWhenResourceFilenameIsEmpty()
            throws IOException {
        given(mockResource.getFilename())
                .willReturn(null);
        given(mockResource.getInputStream())
                .willReturn(new ByteArrayInputStream("key=value".getBytes()));

        PropertySource<?> source = factory.createPropertySource(null, new EncodedResource(mockResource));
        assertThat(source.getProperty("key"))
                .isEqualTo("value");
    }

    @Test(expected = IllegalStateException.class)
    public void shouldRaiseIllegalStateExceptionWhenYmlFileSpecifiedButYamlLibraryNotPresent()
            throws Throwable {
        given(mockResource.getFilename())
                .willReturn("foo.yml");

        new Sandbox()
                .withClassLoader(maskingClasses(Yaml.class))
                .execute(() -> {
                    factory.createPropertySource(null, new EncodedResource(mockResource));
                });
    }

    @Test(expected = IllegalStateException.class)
    public void shouldRaiseIllegalStateExceptionWhenYamlFileSpecifiedButYamlLibraryNotPresent()
            throws Throwable {
        given(mockResource.getFilename())
                .willReturn("foo.yaml");

        new Sandbox()
                .withClassLoader(maskingClasses(Yaml.class))
                .execute(() -> {
                    factory.createPropertySource(null, new EncodedResource(mockResource));
                });
    }
}
