package automaton.device;

import org.openqa.selenium.Capabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;

public abstract class BaseWebDeviceProvider<Device extends WebDevice>
        implements WebDeviceProvider<Device> {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final String name;

    protected Capabilities capabilities;
    protected Set<String> confidential;

    protected BaseWebDeviceProvider(String name) {
        this.name = name;
    }

    @Override
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
        return confidential;
    }

    public void setConfidential(Set<String> confidential) {
        this.confidential = confidential;
    }

    @Override
    public void accept(Device device) {
        log.info("Provider {} quitting device {}", name, device.getSessionId());
        device.quit();
    }

    @Override
    public void dispose() {
        log.info("Provider {} shut down.", name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseWebDeviceProvider<?> that = (BaseWebDeviceProvider<?>) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(capabilities, that.capabilities) &&
                Objects.equals(confidential, that.confidential);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, capabilities, confidential);
    }
}
