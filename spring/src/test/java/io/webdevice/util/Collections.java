package io.webdevice.util;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;

public class Collections {

    public static Set<String> setOf(String... values) {
        return new LinkedHashSet<>(asList(values));
    }

    public static Map<String, Object> mapOf(String key, Object value) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(key, value);
        return map;
    }
}
