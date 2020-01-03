package io.webdevice.device;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Deque;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static java.lang.String.format;

/**
 * A naive and unoptimized {@link Device} pool
 */
public class DevicePool<Driver extends WebDriver>
        implements DeviceProvider<Driver> {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final BlockingDeque<Device<Driver>> free = new LinkedBlockingDeque<>();
    private final BlockingDeque<Device<Driver>> used = new LinkedBlockingDeque<>();
    private final String name;
    private final DeviceProvider<Driver> provider;

    public DevicePool(String name, DeviceProvider<Driver> provider) {
        this.provider = provider;
        this.name = name;
    }

    /**
     * Acquires a {@link Device} for exclusive use
     *
     * @return a {@link Device} for exclusive use
     */
    @Override
    public synchronized Device<Driver> get() {
        Device<Driver> device = free.poll();
        if (device == null) {
            device = create();
        } else {
            if (!device.usable()) {
                log.info("Device {} in {} pool is not usable", device.getSessionId(), name);
                release(device);
                device = create();
            }
        }
        used.push(device);
        log.info("Acquired {} in {} pool", device.getSessionId(), name);
        logStats();
        return device;
    }

    /**
     * Marks the {@link Device} as free for use
     *
     * @param device The {@link Device} to be made available
     */
    @Override
    public synchronized void accept(Device<Driver> device) {
        log.info("Removing device {} from used deque in {} pool", device.getSessionId(), name);
        if (used.remove(device)) {
            log.info("Adding device {} to free deque in {} pool", device.getSessionId(), name);
            free.push(device);
        }
        logStats();
    }

    @Override
    public synchronized void dispose() {
        log.info("Shutting down {} pool...", name);
        drain(free);
        drain(used);
        log.info("Pool {} shut down.", name);
    }

    private Device<Driver> create() {
        log.info("Obtaining new device from provider {}...", name);
        Device<Driver> device = provider.get();
        log.info("Obtained new device {} from provider {}.", device.getSessionId(), name);
        return device;
    }

    private void release(Device<Driver> device) {
        SessionId sessionId = device.getSessionId();
        log.info("Releasing {} from use in {} pool", device.getSessionId(), name);
        try {
            provider.accept(device);
        } catch (Exception e) {
            log.warn(format("Failure releasing device %s from %s pool", sessionId, name), e);
        }
    }

    private void drain(Deque<Device<Driver>> devices) {
        for (Device<Driver> device = devices.poll();
             device != null;
             device = devices.poll()) {
            release(device);
        }
    }

    private void logStats() {
        log.info("DevicePool: {}, Free: {}, Used: {}", name, free.size(), used.size());
    }
}
