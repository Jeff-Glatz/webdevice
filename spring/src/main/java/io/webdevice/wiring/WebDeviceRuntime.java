package io.webdevice.wiring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(WebDeviceRegistrar.class)
@ComponentScan("io.webdevice.wiring")
public class WebDeviceRuntime {
}
