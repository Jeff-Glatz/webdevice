package automaton.device;

import org.openqa.selenium.remote.SessionId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.net.MalformedURLException;
import java.net.URL;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;
import static java.lang.String.format;

@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class Browser
        extends WebDriverDecorator<WebDevice>
        implements WebDevice {
    private final ApplicationContext context;
    private final URL baseUrl;

    @Autowired
    public Browser(ApplicationContext context, URL baseUrl) {
        this.context = context;
        this.baseUrl = baseUrl;
    }

    public URL getBaseUrl() {
        return baseUrl;
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
        delegate = provider(name)
                .get();
        log.info("Acquired {} browser {}", name, delegate.getSessionId());
        return this;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public SessionId getSessionId() {
        return delegate.getSessionId();
    }

    public void home() {
        delegate.navigate().to(baseUrl);
    }

    public void navigateTo(String relativePath) {
        delegate.navigate().to(absolute(relativePath));
    }

    @PreDestroy
    public void release() {
        try {
            if (delegate != null) {
                log.info("Releasing {} browser {}...", delegate.getName(), delegate.getSessionId());
                provider(delegate.getName())
                        .accept(delegate);
            }
            log.info("Browser released.");
        } finally {
            delegate = null;
        }
    }

    private WebDeviceProvider<WebDevice> provider(String name) {
        return context.getBean(name, WebDeviceProvider.class);
    }
}
