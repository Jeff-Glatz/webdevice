package io.webdevice.driver;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.MutableCapabilities;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.lang.String.valueOf;
import static java.util.Collections.emptySet;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;

// TODO: How to protected confidential capabilities
public class ConfidentialCapabilities {
    private static final String CONFIDENTIAL_CAPABILITY = "meta:confidential";
    private static final String MASK = "********";

    /**
     * Retrieves the set of capabilities that should be considered confidential.
     *
     * @param capabilities The {@link Capabilities} instance to check.
     * @return The set of capabilities considered confidential.
     */
    public static Set<String> confidential(Capabilities capabilities) {
        Set<String> confidential = (Set<String>) capabilities
                .getCapability(CONFIDENTIAL_CAPABILITY);
        return confidential != null ? confidential : emptySet();
    }

    /**
     * Marks the supplied {@link MutableCapabilities} instance with the set of capabilities
     * that should be considered confidential.
     *
     * @param capabilities The {@link MutableCapabilities} instance to modify.
     * @param confidential The set of capabilities considered confidential.
     * @return The supplied {@link MutableCapabilities} instance.
     */
    public static MutableCapabilities mark(MutableCapabilities capabilities, Set<String> confidential) {
        capabilities.setCapability(CONFIDENTIAL_CAPABILITY, confidential);
        return capabilities;
    }

    /**
     * Mimics {@link MutableCapabilities#toString()} behavior, masking capability values marked
     * as confidential.
     *
     * @param capabilities The {@link Capabilities} instance to stringify.
     * @return The string representation of the supplied {@link Capabilities} instance.
     */
    public static String mask(Capabilities capabilities) {
        return mask(new IdentityHashMap<>(), capabilities.asMap(), confidential(capabilities));
    }

    private static String mask(Map<Object, String> seen, Object stringify, Set<String> confidential) {
        if (stringify == null) {
            return "null";
        }

        StringBuilder value = new StringBuilder();

        if (stringify.getClass().isArray()) {
            value.append("[");
            value.append(
                    Stream.of((Object[]) stringify)
                            .map(item -> mask(seen, item, confidential))
                            .collect(joining(", ")));
            value.append("]");
        } else if (stringify instanceof Collection) {
            value.append("[");
            value.append(
                    ((Collection<?>) stringify).stream()
                            .map(item -> mask(seen, item, confidential))
                            .collect(joining(", ")));
            value.append("]");
        } else if (stringify instanceof Map) {
            value.append("{");
            value.append(
                    ((Map<?, ?>) stringify).entrySet().stream()
                            .filter(entry -> !entry.getKey().equals(CONFIDENTIAL_CAPABILITY))
                            .sorted(comparing(entry -> valueOf(entry.getKey())))
                            .map(entry -> entry.getKey() + ": " +
                                    (!confidential.contains(entry.getKey()) ?
                                            mask(seen, entry.getValue(), confidential) :
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
