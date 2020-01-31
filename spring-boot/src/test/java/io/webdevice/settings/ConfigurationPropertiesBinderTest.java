package io.webdevice.settings;

import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import static io.bestquality.util.MapBuilder.mapOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class ConfigurationPropertiesBinderTest
        extends UnitTest {
    @Mock
    private ConfigurableEnvironment mockEnvironment;
    private MutablePropertySources sources;
    private ConfigurationPropertiesBinder binder;

    @Before
    public void setUp() {
        sources = new MutablePropertySources();
        given(mockEnvironment.getPropertySources())
                .willReturn(sources);

        binder = new ConfigurationPropertiesBinder();
    }

    @Test
    public void shouldAlwaysAttachConfigurationPropertySourcesBeforeAttemptingBind() {
        MapPropertySource source = new MapPropertySource("test", mapOf(String.class, Object.class)
                .with("webdevice.default-device", "Remote")
                .build());

        sources.addFirst(source);

        assertThat(sources.contains("configurationProperties"))
                .isFalse();
        assertThat(sources.size())
                .isEqualTo(1);
        assertThat(sources.precedenceOf(source))
                .isEqualTo(0);

        Settings actual = binder.from(mockEnvironment);

        assertThat(sources.contains("configurationProperties"))
                .isTrue();
        assertThat(sources.precedenceOf(source))
                .isEqualTo(1);

        assertThat(actual)
                .isEqualTo(new Settings()
                        .withDefaultDevice("Remote"));
    }
}
