package io.webdevice.device;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface DeviceProvider<Driver extends WebDriver>
        extends Supplier<Device<Driver>>, Consumer<Device<Driver>> {

    @Override
    default void accept(Device<Driver> device) {
        final Logger log = LoggerFactory.getLogger(getClass());
        log.info("Quitting device named {} with session {}", device.getName(), device.getSessionId());
        device.perform(WebDriver::quit);
    }
}
