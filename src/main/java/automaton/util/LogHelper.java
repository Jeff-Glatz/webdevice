package automaton.util;

import org.openqa.selenium.Capabilities;

import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;

public class LogHelper {

    public static String mask(Capabilities capabilities, String... masked) {
        HashSet<String> confidential = new HashSet<>(asList(masked));
        confidential.add("password");
        confidential.add("accessKey");
        return mask(new IdentityHashMap<>(), capabilities.asMap(), confidential);
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
                            .sorted(comparing(entry -> valueOf(entry.getKey())))
                            .map(entry -> entry.getKey() + ": " +
                                    (!confidential.contains(entry.getKey()) ? mask(seen, entry.getValue(), confidential) : "********"))
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
