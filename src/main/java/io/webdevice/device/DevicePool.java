package io.webdevice.device;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.SessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
    private final DeviceProvider<Driver> provider;

    @Autowired
    public DevicePool(DeviceProvider<Driver> provider) {
        this.provider = provider;
    }

    @Override
    public String getName() {
        return provider.getName();
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
                log.info("Device {} in {} pool is not usable", device.getSessionId(), getName());
                release(device);
                device = create();
            }
        }
        used.push(device);
        log.info("Acquired {} in {} pool", device.getSessionId(), getName());
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
        log.info("Removing device {} from used deque in {} pool", device.getSessionId(), getName());
        if (used.remove(device)) {
            log.info("Adding device {} to free deque in {} pool", device.getSessionId(), getName());
            free.push(device);
        }
        logStats();
    }

    @Override
    public synchronized void dispose() {
        log.info("Shutting down {} pool...", getName());
        drain(free);
        drain(used);
        log.info("Pool {} shut down.", getName());
    }

    private Device<Driver> create() {
        log.info("Obtaining new device from provider {}...", getName());
        Device<Driver> device = provider.get();
        log.info("Obtained new device {} from provider {}.", device.getSessionId(), getName());
        return device;
    }

    private void release(Device<Driver> device) {
        SessionId sessionId = device.getSessionId();
        log.info("Releasing {} from use in {} pool", device.getSessionId(), getName());
        try {
            provider.accept(device);
        } catch (Exception e) {
            log.warn(format("Failure releasing device %s from %s pool", sessionId, getName()), e);
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
        log.info("Pool: {}, Free: {}, Used: {}", getName(), free.size(), used.size());
    }
}
