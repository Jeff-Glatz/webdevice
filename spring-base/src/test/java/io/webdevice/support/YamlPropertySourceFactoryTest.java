package io.webdevice.support;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class YamlPropertySourceFactoryTest {
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
}
