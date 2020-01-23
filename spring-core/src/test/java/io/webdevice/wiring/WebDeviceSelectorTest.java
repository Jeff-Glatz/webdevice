package io.webdevice.wiring;

import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

public class WebDeviceSelectorTest
        extends UnitTest {
    @Mock
    private Environment mockEnvironment;
    @Mock
    private AnnotationMetadata mockAnnotationMetadata;

    private WebDeviceSelector selector;

    @Before
    public void setUp() {
        selector = new WebDeviceSelector(mockEnvironment);
        selector.initialize();
    }

    @Test
    public void shouldImportCucumberGlueScopeWiring() {
        given(mockEnvironment.getProperty(eq("webdevice.scope"), any(String.class)))
                .willReturn("cucumber-glue");

        assertThat(selector.selectImports(mockAnnotationMetadata))
                .containsExactly("io.webdevice.wiring.CucumberGlueScopeWiring");
    }

    @Test
    public void shouldImportWebDeviceScopeWiring() {
        given(mockEnvironment.getProperty(eq("webdevice.scope"), any(String.class)))
                .willReturn("webdevice");

        assertThat(selector.selectImports(mockAnnotationMetadata))
                .containsExactly("io.webdevice.wiring.WebDeviceScopeWiring");
    }

    @Test
    public void shouldImportSingletonScopeWiring() {
        given(mockEnvironment.getProperty(eq("webdevice.scope"), any(String.class)))
                .willReturn("singleton");

        assertThat(selector.selectImports(mockAnnotationMetadata))
                .containsExactly("io.webdevice.wiring.DefaultScopeWiring");
    }

    @Test
    public void shouldImportApplicationScopeWiring() {
        given(mockEnvironment.getProperty(eq("webdevice.scope"), any(String.class)))
                .willReturn("application");

        assertThat(selector.selectImports(mockAnnotationMetadata))
                .containsExactly("io.webdevice.wiring.DefaultScopeWiring");
    }

    @Test
    public void shouldImportDefaultScopeWiring() {
        given(mockEnvironment.getProperty(eq("webdevice.scope"), any(String.class)))
                .willReturn("");

        assertThat(selector.selectImports(mockAnnotationMetadata))
                .containsExactly("io.webdevice.wiring.DefaultScopeWiring");
    }

    @Test
    public void shouldImportNullScopeWiring() {
        given(mockEnvironment.getProperty(eq("webdevice.scope"), any(String.class)))
                .willReturn(null);

        assertThat(selector.selectImports(mockAnnotationMetadata))
                .containsExactly("io.webdevice.wiring.DefaultScopeWiring");
    }
}
