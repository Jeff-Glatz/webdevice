package io.webdevice.driver;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.lang.String.valueOf;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.springframework.util.ReflectionUtils.makeAccessible;

public class ConfidentialWebDriver
        extends RemoteWebDriver {
    private final Set<String> confidential;

    public ConfidentialWebDriver(URL remoteAddress, Capabilities capabilities, Set<String> confidential) {
        super(remoteAddress, capabilities);
        this.confidential = new LinkedHashSet<>(confidential);
    }

    @Override
    protected void startSession(Capabilities capabilities) {
        super.startSession(capabilities);
        try {
            Field field = getClass().getDeclaredField("capabilities");
            makeAccessible(field);
            field.set(this, new ConfidentialCapabilities(capabilities, confidential));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static class ConfidentialCapabilities
            extends MutableCapabilities {
        private static final String MASK = "********";

        private final Set<String> confidential;

        public ConfidentialCapabilities(Capabilities other, Set<String> confidential) {
            super(other);
            this.confidential = confidential;
        }

        /**
         * Mimics {@link MutableCapabilities#toString()} behavior, masking capability values marked
         * as confidential.
         *
         * @return The string representation of this {@link Capabilities} instance.
         */
        public String toString() {
            return mask(new IdentityHashMap<>(), asMap());
        }

        private String mask(Map<Object, String> seen, Object stringify) {
            if (stringify == null) {
                return "null";
            }

            StringBuilder value = new StringBuilder();

            if (stringify.getClass().isArray()) {
                value.append("[");
                value.append(
                        Stream.of((Object[]) stringify)
                                .map(item -> mask(seen, item))
                                .collect(joining(", ")));
                value.append("]");
            } else if (stringify instanceof Collection) {
                value.append("[");
                value.append(
                        ((Collection<?>) stringify).stream()
                                .map(item -> mask(seen, item))
                                .collect(joining(", ")));
                value.append("]");
            } else if (stringify instanceof Map) {
                value.append("{");
                value.append(
                        ((Map<?, ?>) stringify).entrySet().stream()
                                .sorted(comparing(entry -> valueOf(entry.getKey())))
                                .map(entry -> entry.getKey() + ": " +
                                        (!confidential.contains(entry.getKey()) ?
                                                mask(seen, entry.getValue()) :
                                                MASK))
                                .collect(joining(", ")));
                value.append("}");
            } else {
                String s = valueOf(stringify);
                if (s.length() > 30) {
                    value.append(s.substring(0, 27)).append("...");
                } else {
                    value.append(s);
                }
            }

            seen.put(stringify, value.toString());
            return value.toString();
        }
    }
}
