package io.webdevice.support;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.CommandExecutor;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * This class exists only to prevent sensitive capabilities from being leaked when {@link #toString()}
 * is called on
 */
public class ProtectedWebDriver
        extends RemoteWebDriver
        implements Supplier<Collection<String>> {
    private final Collection<String> confidential;

    public ProtectedWebDriver(URL remoteAddress, Capabilities capabilities, Collection<String> confidential) {
        super(remoteAddress, capabilities);
        this.confidential = confidential;
    }

    public ProtectedWebDriver(CommandExecutor executor, Capabilities capabilities, Collection<String> confidential) {
        super(executor, capabilities);
        this.confidential = confidential;
    }

    @Override
    public Collection<String> get() {
        return confidential;
    }

    @Override
    protected void startSession(Capabilities capabilities) {
        super.startSession(capabilities);
        try {
            Field field = RemoteWebDriver.class.getDeclaredField("capabilities");
            field.setAccessible(true);
            field.set(this, new ProtectedCapabilities(getCapabilities(), this));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
