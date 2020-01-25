package io.webdevice.support;

import io.webdevice.settings.SettingsBinder;
import io.webdevice.support.AnnotationAttributes.ConversionFailedException;
import io.webdevice.test.UnitTest;
import io.webdevice.wiring.EnableWebDevice;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.core.type.AnnotationMetadata;

import java.util.HashMap;
import java.util.Map;

import static io.webdevice.support.AnnotationAttributes.attributesOf;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

public class AnnotationAttributesTest
        extends UnitTest {
    @Mock
    private AnnotationMetadata mockMetadata;
    private AnnotationAttributes attributes;
    private Map<String, Object> attributeMap;

    @Before
    public void setUp() {
        attributeMap = new HashMap<>();
        attributeMap.put("integer", 1);
        attributeMap.put("class", SettingsBinder.class);
        attributeMap.put("string", "true");

        attributes = new AnnotationAttributes(EnableWebDevice.class, attributeMap);
    }

    @Test
    public void shouldCheckForPresenceOfValues() {
        assertThat(attributes.hasValue("string"))
                .isTrue();
        assertThat(attributes.hasValue("not-here"))
                .isFalse();
    }

    @Test
    public void shouldReturnStringValues() {
        assertThat(attributes.valueOf("string"))
                .isEqualTo("true");
        assertThat(attributes.valueOf("not-here"))
                .isNull();
        assertThat(attributes.valueOf("not-here", () -> "yes"))
                .isEqualTo("yes");
    }

    @Test
    public void shouldReturnTypedValues() {
        assertThat(attributes.valueOf("integer", Integer.class))
                .isEqualTo(1);
        assertThat(attributes.valueOf("not-here", Integer.class))
                .isNull();
        assertThat(attributes.valueOf("not-here", Integer.class, () -> 1))
                .isEqualTo(1);
    }

    @Test
    public void shouldReturnConvertedValues() {
        assertThat((Boolean) attributes.valueOf("string", String.class, Boolean::new))
                .isTrue();
        assertThat((Boolean) attributes.valueOf("not-here", String.class, Boolean::new))
                .isNull();
        assertThat(attributes.valueOf("not-here", String.class, Boolean::new, () -> TRUE))
                .isTrue();
    }

    @Test
    public void shouldUseDefaultValueSupplierWhenConverterReturnsNull() {
        assertThat(attributes.valueOf("string", String.class, s -> null, () -> FALSE))
                .isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldPropagateRuntimeExceptionWhileConvertingValue() {
        attributes.valueOf("class", Class.class, (cls) -> {
            throw new IllegalArgumentException();
        });
    }

    @Test(expected = ConversionFailedException.class)
    public void shouldWrapCheckedExceptionWhileConvertingValue() {
        attributes.valueOf("class", Class.class, (cls) -> {
            throw new Exception("boom");
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldPropagateRuntimeExceptionWhileSupplyingDefaultValue() {
        attributes.valueOf("class", Class.class,
                (cls) -> null,
                () -> {
                    throw new IllegalArgumentException("boom");
                });
    }

    @Test(expected = ConversionFailedException.class)
    public void shouldWrapCheckedExceptionWhileSupplyingDefaultValue() {
        attributes.valueOf("class", Class.class,
                (cls) -> null,
                () -> {
                    throw new Exception("boom");
                });
    }

    @Test
    public void shouldReturnInstanceWithEmptyMapWhenNoAnnotationPresent() {
        given(mockMetadata.getAnnotationAttributes(EnableWebDevice.class.getName()))
                .willReturn(null);

        AnnotationAttributes attributes = attributesOf(EnableWebDevice.class, mockMetadata);

        assertThat(attributes.isEmpty())
                .isTrue();
    }

    @Test
    public void shouldReturnInstanceWithAttributeMapWhenAnnotationPresent() {
        given(mockMetadata.getAnnotationAttributes(EnableWebDevice.class.getName()))
                .willReturn(attributeMap);

        AnnotationAttributes attributes = attributesOf(EnableWebDevice.class, mockMetadata);

        assertThat(attributes.isEmpty())
                .isFalse();
        assertThat(attributes.hasValue("string"))
                .isTrue();
        assertThat(attributes.hasValue("not-here"))
                .isFalse();
    }
}
