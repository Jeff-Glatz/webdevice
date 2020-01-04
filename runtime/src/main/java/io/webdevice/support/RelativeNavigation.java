package io.webdevice.support;

import org.openqa.selenium.WebDriver.Navigation;

import java.net.URL;
import java.util.function.Function;

public class RelativeNavigation
        implements Navigation {
    private final Navigation delegate;
    private final Function<String, String> canonicalizer;

    public RelativeNavigation(Navigation delegate, Function<String, String> canonicalizer) {
        this.delegate = delegate;
        this.canonicalizer = canonicalizer;
    }

    @Override
    public void back() {
        delegate.back();
    }

    @Override
    public void forward() {
        delegate.forward();
    }

    @Override
    public void to(String url) {
        delegate.to(canonicalizer.apply(url));
    }

    @Override
    public void to(URL url) {
        delegate.to(url);
    }

    @Override
    public void refresh() {
        delegate.refresh();
    }
}
