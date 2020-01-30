package io.webdevice.support;

import io.bestquality.lang.CheckedFunction;
import io.bestquality.lang.CheckedSupplier;
import org.springframework.core.convert.ConversionException;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static io.bestquality.lang.CheckedFunction.identity;
import static io.webdevice.support.AnnotationAttributes.ConversionFailedException.fromConverter;
import static io.webdevice.support.AnnotationAttributes.ConversionFailedException.fromSupplier;
import static java.lang.String.format;
import static java.util.stream.Collectors.toMap;

public class AnnotationAttributes {
    private final Class<? extends Annotation> annotation;
    private final Map<String, Object> attributes;

    public AnnotationAttributes(Class<? extends Annotation> annotation,
                                Map<String, Object> attributes) {
        this.annotation = annotation;
        this.attributes = attributes;
    }

    public Map<String, Object> asMap() {
        return attributes;
    }

    public PropertySource<?> asPropertySource() {
        return new MapPropertySource(annotation.getSimpleName(), attributes);
    }

    public PropertySource<?> asPropertySource(Predicate<Map.Entry<String, Object>> filter,
                                              Function<Map.Entry<String, Object>, String> keyMapper,
                                              Function<Map.Entry<String, Object>, Object> valueMapper) {
        return new MapPropertySource(annotation.getSimpleName(), attributes.entrySet().stream()
                .filter(filter)
                .collect(toMap(keyMapper, valueMapper)));
    }

    public boolean isEmpty() {
        return attributes.isEmpty();
    }

    public boolean hasValue(String attribute) {
        Object value = attributes.get(attribute);
        return !ObjectUtils.isEmpty(value);
    }

    public <T, R> R valueOf(String attribute, Class<T> type, CheckedFunction<T, R> converter,
                            CheckedSupplier<R> defaultValue) {
        T value = type.cast(attributes.get(attribute));
        if (!ObjectUtils.isEmpty(value)) {
            try {
                R converted = converter.apply(value);
                if (converted != null) {
                    return converted;
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw fromConverter(e, annotation, attribute, value);
            }
        }
        try {
            return defaultValue != null
                    ? defaultValue.get()
                    : null;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw fromSupplier(e, annotation, attribute, value);
        }
    }

    public <T, R> R valueOf(String attribute, Class<T> type, CheckedFunction<T, R> converter) {
        return valueOf(attribute, type, converter, null);
    }

    public <T> T valueOf(String attribute, Class<T> type, CheckedSupplier<T> defaultValue) {
        return valueOf(attribute, type, identity(), defaultValue);
    }

    public <T> T valueOf(String attribute, Class<T> type) {
        return valueOf(attribute, type, identity(), null);
    }

    public String valueOf(String attribute, CheckedSupplier<String> defaultValue) {
        return valueOf(attribute, String.class, identity(), defaultValue);
    }

    public String valueOf(String attribute) {
        return valueOf(attribute, String.class, identity(), null);
    }

    public static AnnotationAttributes attributesOf(Class<? extends Annotation> annotation,
                                                    AnnotationMetadata metadata) {
        Map<String, Object> attributes = metadata.getAnnotationAttributes(annotation.getName());
        return attributes != null
                ? new AnnotationAttributes(annotation, attributes)
                : new AnnotationAttributes(annotation, new HashMap<>());
    }

    public static class ConversionFailedException
            extends ConversionException {
        public ConversionFailedException(String message, Throwable cause) {
            super(message, cause);
        }

        public static ConversionFailedException fromConverter(Throwable cause,
                                                              Class<? extends Annotation> annotation,
                                                              String attribute,
                                                              Object value) {
            String message = format("Failed to convert %s.%s = %s",
                    annotation.getSimpleName(), attribute, value);
            return new ConversionFailedException(message, cause);

        }

        public static ConversionFailedException fromSupplier(Throwable cause,
                                                             Class<? extends Annotation> annotation,
                                                             String attribute,
                                                             Object value) {
            String message = format("Default value supplier failed attempting to convert %s.%s = %s",
                    annotation.getSimpleName(), attribute, value);
            return new ConversionFailedException(message, cause);
        }
    }
}
