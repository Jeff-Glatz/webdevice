package io.webdevice.device;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;

public class StubWebDriver
        extends RemoteWebDriver {

    public StubWebDriver() {
    }

    public StubWebDriver(Capabilities capabilities) {
        super(capabilities);
    }

    public StubWebDriver(CommandExecutor executor, Capabilities capabilities) {
        super(executor, capabilities);
    }

    public StubWebDriver(URL remoteAddress, Capabilities capabilities) {
        super(remoteAddress, capabilities);
    }

    @Override
    protected void startSession(Capabilities capabilities) {
    }
}
