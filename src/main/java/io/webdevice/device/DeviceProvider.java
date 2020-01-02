package io.webdevice.device;

import org.openqa.selenium.WebDriver;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface DeviceProvider<Driver extends WebDriver>
        extends Supplier<Device<Driver>>, Consumer<Device<Driver>> {

    String getName();

    @PostConstruct
    default void initialize() {
    }

    @PreDestroy
    default void dispose() {
    }
}
