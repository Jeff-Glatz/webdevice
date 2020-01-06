package engineering.bestquality.protocol.mock;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * This class handles all URLs using the "mock:" scheme
 */
public class Handler
        extends URLStreamHandler {

    @Override
    public URLConnection openConnection(URL url)
            throws IOException {
        return MockConnectionRegistry.findMockConnection(url);
    }
}