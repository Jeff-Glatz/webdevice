package io.webdevice.driver;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.lang.String.valueOf;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;

public class ProtectedCapabilities
        extends MutableCapabilities {
    private static final String MASK = "********";

    private final Set<String> confidential;

    public ProtectedCapabilities(Capabilities other, Set<String> confidential) {
        super(other);
        this.confidential = confidential;
    }

    public static String mask(Capabilities capabilities, Set<String> confidential) {
        return new ProtectedCapabilities(capabilities, confidential)
                .toString();
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
        } else if (stringify instanceof Capabilities) {
            value.append("{");
            value.append(
                    ((Capabilities) stringify).asMap().entrySet().stream()
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
