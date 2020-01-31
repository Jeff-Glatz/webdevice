package io.webdevice.settings;

import java.util.StringJoiner;

import static org.springframework.util.StringUtils.capitalize;

class PropertyPath {

    private PropertyPath() {
    }

    /**
     * Normalizes a WebDevice environment property path to be compatible with core spring's
     * {@link org.springframework.validation.DataBinder}
     *
     * @param path The environment property path to normalize
     * @return A normalized property path compatible with
     *         {@link org.springframework.validation.DataBinder}
     */
    public static String normalize(String path) {
        path = path.replaceAll("(devices)\\.(\\w+)\\.", "$1[$2].");
        path = path.replaceAll("(capabilities)\\.(\\w+)", "$1[$2]");
        path = path.replaceAll("(extra[-oO]+ptions)\\.(\\w+)", "$1[$2]");
        path = diskebab(path);
        return path;
    }

    /**
     * Removes the '-' character (skewer) in a kebab-case string and camel
     * cases the resultant string.
     *
     * @param path The path to de-skewer.
     * @return The resultant camel-case string.
     */
    private static String deskewer(String path) {
        StringJoiner joiner = new StringJoiner("");
        String[] parts = path.split("-");
        joiner.add(parts[0]);
        if (parts.length > 1) {
            for (int i = 1; i < parts.length; i++) {
                joiner.add(capitalize(parts[i]));
            }
        }
        return joiner.toString();
    }

    /**
     * Removes any kebab-casing in the supplied string, converting
     * it into a camel-case string compatible with the core spring
     * {@link org.springframework.validation.DataBinder}.
     *
     * @param path The path to convert
     * @return A camel-case string suitable for property path binding.
     */
    private static String diskebab(String path) {
        StringJoiner joiner = new StringJoiner(".");
        StringBuilder builder = new StringBuilder(path.length());
        // Break the property path into it's component parts
        for (String part : path.split("\\.")) {
            String[] split = part.split("\\[");
            builder.append(deskewer(split[0]));
            if (split.length == 2) {
                builder.append("[").append(split[1]);
            }
            joiner.add(builder);
            builder.setLength(0);
        }
        return joiner.toString();
    }
}
