package io.webdevice.device;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.UUID.randomUUID;

public class Devices {

    public static SessionId randomSessionId() {
        return new SessionId(randomUUID());
    }

    public static <Driver extends WebDriver> Function<Driver, SessionId> fixedSession(SessionId sessionId) {
        return (driver) -> sessionId;
    }

    public static <Driver extends WebDriver> Function<Driver, SessionId> fixedSession() {
        return fixedSession(randomSessionId());
    }

    public static <Driver extends RemoteWebDriver> Function<Driver, SessionId> remoteSession() {
        return RemoteWebDriver::getSessionId;
    }

    public static <Driver extends RemoteWebDriver> Device<Driver> remote(String name,
                                                                         Driver driver,
                                                                         Function<Driver, Boolean> usable) {
        return new Device<>(name, driver, remoteSession(), usable);
    }

    public static <Driver extends WebDriver> Device<Driver> direct(String name,
                                                                   Driver driver,
                                                                   Function<Driver, Boolean> usable) {
        return new Device<>(name, driver, fixedSession(), usable);
    }

    public static <Driver extends WebDriver> DeviceProvider<Driver> provider(Supplier<Device<Driver>> supplier) {
        return supplier::get;
    }

    public static <Driver extends WebDriver> DeviceProvider<Driver> provider(String device,
                                                                             Supplier<Driver> supplier,
                                                                             Function<Driver, SessionId> session,
                                                                             Function<Driver, Boolean> usable) {
        return () -> new Device<>(device, supplier.get(), session, usable);
    }

    public static <Driver extends RemoteWebDriver> DeviceProvider<Driver> directProvider(String device,
                                                                                         Supplier<Driver> supplier,
                                                                                         Function<Driver, Boolean> usable) {
        return () -> direct(device, supplier.get(), usable);
    }

    public static <Driver extends RemoteWebDriver> DeviceProvider<Driver> remoteProvider(String device,
                                                                                         Supplier<Driver> supplier,
                                                                                         Function<Driver, Boolean> usable) {
        return () -> remote(device, supplier.get(), usable);
    }
}
