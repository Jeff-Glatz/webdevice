package io.webdevice.driver;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.springframework.util.ReflectionUtils.makeAccessible;

public class ConfidentialWebDriver
        extends RemoteWebDriver {
    private final Set<String> confidential;

    public ConfidentialWebDriver(URL remoteAddress, Capabilities capabilities, Set<String> confidential) {
        super(remoteAddress, capabilities);
        this.confidential = new LinkedHashSet<>(confidential);
    }

    @Override
    protected void startSession(Capabilities capabilities) {
        super.startSession(capabilities);
        try {
            Field field = getClass().getDeclaredField("capabilities");
            makeAccessible(field);
            field.set(this, new ConfidentialCapabilities(capabilities, confidential));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
