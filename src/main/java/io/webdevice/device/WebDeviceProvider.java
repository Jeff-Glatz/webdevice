package io.webdevice.device;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface WebDeviceProvider
        extends Supplier<WebDevice>, Consumer<WebDevice> {

    String getName();

    @PostConstruct
    default void initialize() {
    }

    @PreDestroy
    default void dispose() {
    }
}
