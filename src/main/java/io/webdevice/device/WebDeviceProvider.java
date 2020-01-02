package io.webdevice.device;

import org.openqa.selenium.WebDriver;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface WebDeviceProvider<Driver extends WebDriver>
        extends Supplier<WebDevice<Driver>>, Consumer<WebDevice<Driver>> {

    String getName();

    @PostConstruct
    default void initialize() {
    }

    @PreDestroy
    default void dispose() {
    }
}
