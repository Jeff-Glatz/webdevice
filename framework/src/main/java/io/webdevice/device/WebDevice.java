package io.webdevice.device;

import io.webdevice.support.RelativeNavigation;
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
import org.openqa.selenium.remote.SessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

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

    public boolean acquired() {
        return device != null;
    }

    public String canonicalize(String url) {
        if (!url.contains("://")) {
            String root = baseUrl.toExternalForm();
            if (root.endsWith("/")) {
                if (url.startsWith("/")) {
                    return root.concat(url.substring(1));
                } else {
                    return root.concat(url);
                }
            } else {
                if (url.startsWith("/")) {
                    return root.concat(url);
                } else {
                    return root.concat("/").concat(url);
                }
            }
        }
        // Already absolute
        return url;
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
        device.perform(driver -> driver.navigate().to(canonicalize(relativePath)));
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

    // HasCapabilities delegate

    @Override
    public Capabilities getCapabilities() {
        return device.as(HasCapabilities.class)
                .getCapabilities();
    }

    // JavascriptExecutor delegates

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

    // TakesScreenshot delegate

    @Override
    public <X> X getScreenshotAs(OutputType<X> target)
            throws WebDriverException {
        return device.as(TakesScreenshot.class)
                .getScreenshotAs(target);
    }

    // WebDriver delegates

    @Override
    public void get(String url) {
        device.as(WebDriver.class)
                .get(canonicalize(url));
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
        // Only delegate when there is more than one window open, otherwise quit will be invoked
        if (getWindowHandles().size() > 1) {
            device.as(WebDriver.class)
                    .close();
        } else {
            log.warn("Only the provider of the current device should manage the driver's lifecycle");
        }
    }

    @Override
    public void quit() {
        log.warn("Only the provider of the current device should manage the driver's lifecycle");
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
        return new RelativeNavigation(navigation, this::canonicalize);
    }

    @Override
    public Options manage() {
        return device.as(WebDriver.class)
                .manage();
    }

    // Interactive delegates

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
            log.info("Releasing WebDevice ...");
            if (device != null) {
                String name = device.getName();
                SessionId sessionId = device.getSessionId();
                log.info("Releasing {} device {}...", name, sessionId);
                registry.release(device);
                log.info("{} device {} released.", name, sessionId);
            }
            log.info("WebDevice released.");
        } finally {
            device = null;
        }
    }

    Device<?> device() {
        return device;
    }
}
