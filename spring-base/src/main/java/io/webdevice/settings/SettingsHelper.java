package io.webdevice.settings;

import static org.springframework.util.StringUtils.capitalize;

class SettingsHelper {

    private static String camelCase(String path) {
        StringBuilder builder = new StringBuilder();
        for (String part : path.split("-")) {
            builder.append(builder.length() == 0
                    ? part :
                    capitalize(part));
        }
        return builder.toString();
    }

    public static String normalize(String path) {
        path = camelCase(path);
        path = path.replaceAll("(devices)\\.(\\w+)\\.", "$1[$2].");
        path = path.replaceAll("(capabilities)\\.(\\w+)", "$1[$2]");
        path = path.replaceAll("(extraOptions)\\.(\\w+)", "$1[$2]");
        return path;
    }
}
