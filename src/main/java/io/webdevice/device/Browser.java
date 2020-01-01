package io.webdevice.device;

import io.webdevice.driver.WebDriverDecorator;
import io.webdevice.wiring.Settings;
import org.openqa.selenium.remote.SessionId;
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
public class Browser
        extends WebDriverDecorator<WebDevice>
        implements WebDevice {
    private final WebDeviceProviders providers;
    private final Settings settings;

    private URL baseUrl;

    @Autowired
    public Browser(WebDeviceProviders providers, Settings settings) {
        this.providers = providers;
        this.settings = settings;
    }

    @PostConstruct
    public void initialize() {
        // Initialize baseUrl from settings, but this can be redefined in hooks or steps as needed
        setBaseUrl(settings.getBaseUrl());
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
        if (delegate != null) {
            throw new IllegalStateException("Browser has already been acquired for the current scenario");
        }
        log.info("Acquiring {} browser...", name);
        delegate = providers.providerOf(name)
                .get();
        log.info("Acquired {} browser {}", name, delegate.getSessionId());
        return this;
    }

    public Browser use() {
        return use(settings.getDefaultDevice());
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public SessionId getSessionId() {
        return delegate.getSessionId();
    }

    @Override
    public boolean usable() {
        return delegate.usable();
    }

    public Browser home() {
        delegate.navigate().to(baseUrl);
        return this;
    }

    public Browser navigateTo(String relativePath) {
        delegate.navigate().to(absolute(relativePath));
        return this;
    }

    @PreDestroy
    public void release() {
        try {
            if (delegate != null) {
                log.info("Releasing {} browser {}...", delegate.getName(), delegate.getSessionId());
                providers.providerOf(delegate)
                        .accept(delegate);
            }
            log.info("Browser released.");
        } finally {
            delegate = null;
        }
    }
}
