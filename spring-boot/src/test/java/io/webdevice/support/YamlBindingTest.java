package io.webdevice.support;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.convert.ApplicationConversionService;

import static org.assertj.core.api.Assertions.assertThat;

public class YamlBindingTest {
    private YamlBinding configuration;

    @Before
    public void setUp() {
        configuration = new YamlBinding();
    }

    @Test
    public void shouldDefineApplicationConversionService() {
        assertThat(configuration.conversionService())
                .isInstanceOf(ApplicationConversionService.class);
    }
}
