package io.webdevice.device;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

public class RemoteWebDevice
        extends BaseWebDevice<RemoteWebDriver> {

    public RemoteWebDevice(RemoteWebDriver driver, String name) {
        super(driver, name);
    }

    @Override
    public SessionId getSessionId() {
        return delegate.getSessionId();
    }

    @Override
    public boolean usable() {
        // TODO: How to determine liveness?
        return true;
    }
}
