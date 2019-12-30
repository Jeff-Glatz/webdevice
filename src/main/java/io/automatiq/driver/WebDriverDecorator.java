package io.automatiq.driver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public class WebDriverDecorator<Driver extends WebDriver>
        implements WebDriver {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected Driver delegate;

    public WebDriverDecorator(Driver delegate) {
        this.delegate = delegate;
    }

    public WebDriverDecorator() {
        this(null);
    }

    public Driver getDelegate() {
        return delegate;
    }

    @Override
    public void get(String s) {
        delegate.get(s);
    }

    @Override
    public String getCurrentUrl() {
        return delegate.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return delegate.getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return delegate.findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        return delegate.findElement(by);
    }

    @Override
    public String getPageSource() {
        return delegate.getPageSource();
    }

    @Override
    public void close() {
        delegate.close();
    }

    @Override
    public void quit() {
        delegate.quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        return delegate.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return delegate.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return delegate.switchTo();
    }

    @Override
    public Navigation navigate() {
        return delegate.navigate();
    }

    @Override
    public Options manage() {
        return delegate.manage();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WebDriverDecorator<?> that = (WebDriverDecorator<?>) o;
        return Objects.equals(delegate, that.delegate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(delegate);
    }
}
