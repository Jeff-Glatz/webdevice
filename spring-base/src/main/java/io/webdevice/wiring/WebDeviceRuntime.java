package io.webdevice.wiring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextClosedEvent;

import static io.webdevice.wiring.WebDeviceScope.scope;

@Import(WebDeviceRegistrar.class)
@Configuration("webdevice.WebDeviceRuntime")
public class WebDeviceRuntime {
    private final Logger log = LoggerFactory.getLogger(WebDeviceRuntime.class);

    @Bean("webdevice.WebDeviceRuntimeDisposer")
    public ApplicationListener<ContextClosedEvent> webDeviceRuntimeDisposer() {
        return (event) -> {
            WebDeviceScope scope = scope(event.getApplicationContext());
            if (scope != null && !scope.isEmpty()) {
                log.info("Disposing webdevice scope on context closed event");
                scope.dispose();
            }
        };
    }
}
