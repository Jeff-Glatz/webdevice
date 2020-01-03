package io.webdevice.device;

import io.webdevice.support.Navigator;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Interactive;
import org.openqa.selenium.interactions.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.lang.String.format;

public class WebDevice
        implements WebDriver, JavascriptExecutor, HasCapabilities, Interactive, TakesScreenshot {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final DeviceRegistry registry;

    private URL baseUrl;
    private String defaultDevice;
    private boolean eager = false;
    private boolean strict = true;
    private Device<?> device;

    public WebDevice(DeviceRegistry registry) {
        this.registry = registry;
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    public WebDevice withBaseUrl(URL baseUrl) {
        setBaseUrl(baseUrl);
        return this;
    }

    public String getDefaultDevice() {
        return defaultDevice;
    }

    public void setDefaultDevice(String defaultDevice) {
        this.defaultDevice = defaultDevice;
    }

    public WebDevice withDefaultDevice(String defaultDevice) {
        setDefaultDevice(defaultDevice);
        return this;
    }

    public boolean isEager() {
        return eager;
    }

    public void setEager(boolean eager) {
        this.eager = eager;
    }

    public WebDevice withEager(boolean eager) {
        setEager(eager);
        return this;
    }

    public boolean isStrict() {
        return strict;
    }

    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    public WebDevice withStrict(boolean strict) {
        setStrict(strict);
        return this;
    }

    @PostConstruct
    public void initialize() {
        log.info("Initializing WebDevice...");
        if (eager) {
            log.info("Eagerly acquiring default device");
            useDefault();
        }
        log.info("WebDevice initialized.");
    }

    public URL absolute(String url) {
        if (!url.contains("://")) {
            try {
                return new URL(baseUrl, url);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(format("%s could not be combined with %s",
                        baseUrl, url));
            }
        } else {
            try {
                return new URL(url);
            } catch (MalformedURLException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }

    public WebDevice use(String name) {
        if (device != null) {
            if (strict) {
                throw new IllegalStateException("Browser has already been acquired for the current scenario");
            }
            release();
        }
        log.info("Acquiring {} browser...", name);
        device = registry.provide(name);
        log.info("Acquired {} browser {}", name, device.getSessionId());
        return this;
    }

    public WebDevice useDefault() {
        return use(defaultDevice);
    }

    public WebDevice home() {
        device.perform(driver -> driver.navigate().to(baseUrl));
        return this;
    }

    public WebDevice navigateTo(String relativePath) {
        device.perform(driver -> driver.navigate().to(absolute(relativePath)));
        return this;
    }

    @SuppressWarnings("unchecked")
    public <Driver extends WebDriver> WebDevice perform(Consumer<Driver> consumer) {
        consumer.accept((Driver) device.getDriver());
        return this;
    }

    @SuppressWarnings("unchecked")
    public <Driver extends WebDriver, R> R invoke(Function<Driver, R> function) {
        return function.apply((Driver) device.getDriver());
    }

    @Override
    public Capabilities getCapabilities() {
        return device.as(HasCapabilities.class)
                .getCapabilities();
    }

    @Override
    public Object executeScript(String script, Object... args) {
        return device.as(JavascriptExecutor.class)
                .executeScript(script, args);
    }

    @Override
    public Object executeAsyncScript(String script, Object... args) {
        return device.as(JavascriptExecutor.class)
                .executeAsyncScript(script, args);
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target)
            throws WebDriverException {
        return device.as(TakesScreenshot.class)
                .getScreenshotAs(target);
    }

    @Override
    public void get(String url) {
        if (!url.contains("://")) {
            url = absolute(url)
                    .toExternalForm();
        }
        device.as(WebDriver.class)
                .get(url);
    }

    @Override
    public String getCurrentUrl() {
        return device.as(WebDriver.class)
                .getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return device.as(WebDriver.class)
                .getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return device.as(WebDriver.class)
                .findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        return device.as(WebDriver.class)
                .findElement(by);
    }

    @Override
    public String getPageSource() {
        return device.as(WebDriver.class)
                .getPageSource();
    }

    @Override
    public void close() {
        device.as(WebDriver.class)
                .close();
    }

    @Override
    public void quit() {
        device.as(WebDriver.class)
                .quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        return device.as(WebDriver.class)
                .getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return device.as(WebDriver.class)
                .getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return device.as(WebDriver.class)
                .switchTo();
    }

    @Override
    public Navigation navigate() {
        Navigation navigation = device.as(WebDriver.class)
                .navigate();
        return new Navigator(navigation, this::absolute);
    }

    @Override
    public Options manage() {
        return device.as(WebDriver.class)
                .manage();
    }

    @Override
    public void perform(Collection<Sequence> actions) {
        device.as(Interactive.class)
                .perform(actions);
    }

    @Override
    public void resetInputState() {
        device.as(Interactive.class)
                .resetInputState();
    }

    @PreDestroy
    public void release() {
        try {
            if (device != null) {
                log.info("Releasing {} device {}...", device.getName(), device.getSessionId());
                registry.release(device);
            }
            log.info("WebDevice released.");
        } finally {
            device = null;
        }
    }
}
