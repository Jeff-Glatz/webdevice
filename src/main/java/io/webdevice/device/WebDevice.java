package io.webdevice.device;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;

public interface WebDevice
        extends WebDriver {
    String getName();
    SessionId getSessionId();
    boolean usable();
}
