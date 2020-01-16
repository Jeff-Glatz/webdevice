package io.webdevice.support;

import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openqa.selenium.WebDriver.Navigation;

import java.net.URL;

import static org.mockito.Mockito.verify;

public class RelativeNavigationTest
        extends UnitTest {
    @Mock
    private Navigation mockNavigation;
    private String root;
    private RelativeNavigation navigation;

    @Before
    public void setUp() {
        root = "http://localhost";
        navigation = new RelativeNavigation(mockNavigation, (path) -> root.concat(path));
    }

    @Test
    public void shouldCanonicalizeStringUrls() {
        navigation.to("/foo");

        verify(mockNavigation)
                .to("http://localhost/foo");
    }

    @Test
    public void shouldNotCanonicalizeObjectUrls() throws Exception {
        URL url = new URL("http://remotehost");
        navigation.to(url);

        verify(mockNavigation)
                .to(url);
    }

    @Test
    public void backShouldDelegate() {
        navigation.back();

        verify(mockNavigation)
                .back();
    }

    @Test
    public void forwardShouldDelegate() {
        navigation.forward();

        verify(mockNavigation)
                .forward();
    }

    @Test
    public void refreshShouldDelegate() {
        navigation.refresh();

        verify(mockNavigation)
                .refresh();
    }
}
