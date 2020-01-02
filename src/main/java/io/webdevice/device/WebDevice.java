package io.webdevice.device;

import io.webdevice.wiring.Settings;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Consumer;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;
import static java.lang.String.format;

@Primary
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class WebDevice {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final DeviceRegistry registry;
    private final Settings settings;

    private URL baseUrl;
    private Device<?> device;

    @Autowired
    public WebDevice(DeviceRegistry registry, Settings settings) {
        this.registry = registry;
        this.settings = settings;
    }

    @PostConstruct
    public void initialize() {
        setBaseUrl(settings.getBaseUrl());
        if (settings.isEager()) {
            log.info("Eagerly acquiring default device");
            useDefault();
        }
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

    public URL absolute(String relativePath) {
        try {
            return new URL(baseUrl, relativePath);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(format("%s could not be combined with %s",
                    baseUrl, relativePath));
        }
    }

    public WebDevice use(String name) {
        if (device != null) {
            if (settings.isStrict()) {
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
        return use(settings.getDefaultDevice());
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
        device.perform(driver -> consumer.accept((Driver) driver));
        return this;
    }

    @PreDestroy
    public void release() {
        try {
            if (device != null) {
                log.info("Releasing {} browser {}...", device.getName(), device.getSessionId());
                registry.done(device);
            }
            log.info("Browser released.");
        } finally {
            device = null;
        }
    }
}
