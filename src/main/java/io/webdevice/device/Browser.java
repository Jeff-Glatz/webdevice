package io.webdevice.device;

import io.webdevice.wiring.BrowserSettings;
import io.webdevice.wiring.Settings;
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

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;
import static java.lang.String.format;

@Primary
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class Browser {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    private final WebDeviceProviders providers;
    private final BrowserSettings settings;

    private URL baseUrl;
    private WebDevice<?> device;

    public Browser(WebDeviceProviders providers, BrowserSettings settings) {
        this.providers = providers;
        this.settings = settings;
    }

    @Autowired
    public Browser(WebDeviceProviders providers, Settings settings) {
        this(providers, settings.getBrowser());
    }

    @PostConstruct
    public void initialize() {
        setBaseUrl(settings.getBaseUrl());
        if (settings.isEager()) {
            log.info("Eagerly acquiring default device");
            use();
        }
    }

    public URL getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Browser withBaseUrl(URL baseUrl) {
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

    public Browser use(String name) {
        if (device != null) {
            if (settings.isStrict()) {
                throw new IllegalStateException("Browser has already been acquired for the current scenario");
            }
            release();
        }
        log.info("Acquiring {} browser...", name);
        device = providers.provide(name);
        log.info("Acquired {} browser {}", name, device.getSessionId());
        return this;
    }

    public Browser use() {
        return use(settings.getDefaultDevice());
    }


    public Browser home() {
        device.perform(driver -> driver.navigate().to(baseUrl));
        return this;
    }

    public Browser navigateTo(String relativePath) {
        device.perform(driver -> driver.navigate().to(absolute(relativePath)));
        return this;
    }

    @PreDestroy
    public void release() {
        try {
            if (device != null) {
                log.info("Releasing {} browser {}...", device.getName(), device.getSessionId());
                providers.done(device);
            }
            log.info("Browser released.");
        } finally {
            device = null;
        }
    }
}
