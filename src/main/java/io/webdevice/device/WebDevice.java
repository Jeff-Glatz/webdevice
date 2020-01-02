package io.webdevice.device;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;

import java.util.function.Consumer;

public interface WebDevice<Driver extends WebDriver> {
    String getName();
    Driver getDriver();
    void perform(Consumer<Driver> function);
    SessionId getSessionId();
    boolean usable();
}
