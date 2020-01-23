package io.webdevice.wiring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(WebDeviceRegistrar.class)
public class WebDeviceRuntime {

    @Bean("webdevice.DynamicDependsOn")
    public DynamicDependsOn dependsOn() {
        return new DynamicDependsOn();
    }
}
