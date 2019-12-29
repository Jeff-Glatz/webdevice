package automaton.device;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface WebDeviceProvider<Device extends WebDevice>
        extends Supplier<Device>, Consumer<Device> {

    String getName();

    @PostConstruct
    default void initialize() {
    }

    @PreDestroy
    default void dispose() {
    }
}
