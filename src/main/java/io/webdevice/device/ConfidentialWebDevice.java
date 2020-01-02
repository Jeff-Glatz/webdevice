package io.webdevice.device;

import io.webdevice.driver.ConfidentialWebDriver;
import org.openqa.selenium.remote.SessionId;

public class ConfidentialWebDevice
        extends BaseWebDevice<ConfidentialWebDriver> {

    public ConfidentialWebDevice(ConfidentialWebDriver driver, String name) {
        super(driver, name);
    }

    @Override
    public SessionId getSessionId() {
        return driver.getSessionId();
    }

    @Override
    public boolean usable() {
        // TODO: Investigate the best mechanism for answering this question
        return true;
    }
}
