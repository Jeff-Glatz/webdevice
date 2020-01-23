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
        encodedResource = new EncodedResource(new ClassPathResource("application.yaml"));
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
                .isEqualTo("application.yaml");
    }

    @Test
    public void shouldResolvePropertyValue()
            throws IOException {
        PropertySource<?> source = factory.createPropertySource(null, encodedResource);

        assertThat(source.getProperty("webdevice.base-url"))
                .isEqualTo("https://webdevice.io");
    }
}
