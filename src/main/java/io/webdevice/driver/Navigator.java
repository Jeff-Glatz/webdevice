package io.webdevice.driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;

import java.net.URL;
import java.util.function.Function;

public class Navigator
        implements Navigation {
    private final Navigation delegate;
    private final Function<String, URL> normalizer;

    public Navigator(Navigation delegate, Function<String, URL> normalizer) {
        this.delegate = delegate;
        this.normalizer = normalizer;
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
        delegate.to(normalizer.apply(url));
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
