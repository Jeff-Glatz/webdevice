package io.webdevice.device;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface DeviceProvider<Driver extends WebDriver>
        extends Supplier<Device<Driver>>, Consumer<Device<Driver>> {

    static <Driver extends WebDriver> DeviceProvider<Driver> providing(Supplier<Device<Driver>> supplier) {
        return supplier::get;
    }

    static <Driver extends WebDriver> DeviceProvider<Driver> providing(String device,
                                                                       Supplier<Driver> supplier,
                                                                       Function<Driver, SessionId> session,
                                                                       Function<Driver, Boolean> usable) {
        return () -> new Device<>(device, supplier.get(), session, usable);
    }

    static <Driver extends RemoteWebDriver> DeviceProvider<Driver> providing(String device,
                                                                             Supplier<Driver> supplier,
                                                                             Function<Driver, Boolean> usable) {
        return () -> new Device<>(device, supplier.get(), RemoteWebDriver::getSessionId, usable);
    }

    @PostConstruct
    default void initialize() {
    }

    @Override
    default void accept(Device<Driver> device) {
        final Logger log = LoggerFactory.getLogger(getClass());
        log.info("Quitting device named {} with session {}", device.getName(), device.getSessionId());
        device.perform(WebDriver::quit);
    }

    @PreDestroy
    default void dispose() {
    }
}
