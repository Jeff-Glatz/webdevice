package io.webdevice.wiring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(WebDeviceRegistrar.class)
public class WebDeviceRuntime {
}
