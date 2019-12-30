package automaton.device;

import org.openqa.selenium.ImmutableCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.Objects;

import static automaton.util.LogHelper.mask;

public class RemoteWebDeviceProvider
        extends BaseWebDeviceProvider<RemoteWebDevice> {
    private final URL remoteAddress;

    public RemoteWebDeviceProvider(String name, URL remoteAddress) {
        super(name);
        this.remoteAddress = remoteAddress;
    }

    @Override
    public void initialize() {
        if (capabilities == null) {
            capabilities = new ImmutableCapabilities();
        }
    }

    @Override
    public RemoteWebDevice get() {
        log.info("Providing new device named {} connecting to {} with {}", name, remoteAddress, mask(capabilities));
        return new RemoteWebDevice(new RemoteWebDriver(remoteAddress, capabilities), name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RemoteWebDeviceProvider that = (RemoteWebDeviceProvider) o;
        return Objects.equals(remoteAddress, that.remoteAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), remoteAddress);
    }
}
