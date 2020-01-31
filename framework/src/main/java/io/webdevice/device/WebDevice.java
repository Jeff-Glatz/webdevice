package io.webdevice.device;

import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Interactive;

import javax.annotation.PreDestroy;
import java.util.function.Consumer;
import java.util.function.Function;

public interface WebDevice
        extends WebDriver, JavascriptExecutor, HasCapabilities, Interactive, TakesScreenshot {
    boolean acquired();

    String canonicalize(String url);

    void use(String name);

    void useDefault();

    void home();

    void navigateTo(String relativePath);

    @SuppressWarnings("unchecked")
    <Driver extends WebDriver> void perform(Consumer<Driver> consumer);

    @SuppressWarnings("unchecked")
    <Driver extends WebDriver, R> R invoke(Function<Driver, R> function);

    @PreDestroy
    void release();
}
