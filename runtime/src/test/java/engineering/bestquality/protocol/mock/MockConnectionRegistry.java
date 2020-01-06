package engineering.bestquality.protocol.mock;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static java.util.regex.Pattern.compile;

public class MockConnectionRegistry {
    private static final Map<Pattern, URLConnection> connections
            = new LinkedHashMap<>();

    public static URLConnection findMockConnection(URL url)
            throws IOException {
        String externalForm = url.toExternalForm();
        return connections.entrySet().stream()
                .filter(entry -> {
                    Pattern pattern = entry.getKey();
                    Matcher matcher = pattern.matcher(externalForm);
                    return matcher.matches();
                })
                .findFirst()
                .orElseThrow(() -> new IOException(format(
                        "Missing mock URLConnection for %s",
                        externalForm)))
                .getValue();
    }

    public static void registerMockConnection(Pattern pattern, URLConnection connection) {
        connections.put(pattern, connection);
    }

    public static void registerMockConnection(URLConnection connection) {
        registerMockConnection(compile(".*"), connection);
    }

    /**
     * Should be called at the end of each test run (i.e. @After)
     */
    public static void clearMockConnections() {
        connections.clear();
    }
}