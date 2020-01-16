package io.webdevice.device;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;

import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.UUID.randomUUID;

public class Devices {

    private Devices() {
    }

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

    public static <Driver extends RemoteWebDriver> Device<Driver> remoteDevice(String name, Driver driver) {
        return new Device<>(name, driver, remoteSession());
    }

    public static <Driver extends WebDriver> Device<Driver> directDevice(String name, Driver driver) {
        return new Device<>(name, driver, fixedSession());
    }

    public static <Driver extends WebDriver> DeviceProvider<Driver> provider(Supplier<Device<Driver>> supplier) {
        return supplier::get;
    }

    public static <Driver extends WebDriver> DeviceProvider<Driver> provider(String device,
                                                                             Supplier<Driver> supplier,
                                                                             Function<Driver, SessionId> session) {
        return () -> new Device<>(device, supplier.get(), session);
    }

    public static <Driver extends WebDriver> DeviceProvider<Driver> directProvider(String device,
                                                                                   Supplier<Driver> supplier) {
        return () -> directDevice(device, supplier.get());
    }

    public static <Driver extends RemoteWebDriver> DeviceProvider<Driver> remoteProvider(String device,
                                                                                         Supplier<Driver> supplier) {
        return () -> remoteDevice(device, supplier.get());
    }
}
