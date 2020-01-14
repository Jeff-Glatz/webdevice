package io.webdevice.device;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Set;

import static io.webdevice.support.ProtectedCapabilities.mask;
import static java.util.Collections.unmodifiableSet;

public abstract class BaseDeviceProvider<Driver extends WebDriver>
        implements DeviceProvider<Driver> {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final Set<String> confidential = new LinkedHashSet<>();
    protected final String name;

    protected Capabilities capabilities;

    protected BaseDeviceProvider(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Capabilities getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Capabilities capabilities) {
        this.capabilities = capabilities;
    }

    public Set<String> getConfidential() {
        return unmodifiableSet(confidential);
    }

    public void setConfidential(Set<String> confidential) {
        this.confidential.clear();
        this.confidential.addAll(confidential);
    }

    public void dispose() {
        log.info("Provider {} shut down.", name);
    }

    protected String maskedCapabilities() {
        return mask(capabilities, confidential);
    }
}
