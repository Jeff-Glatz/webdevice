package io.webdevice.wiring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration("webdevice.WebDeviceRuntime")
@Import(WebDeviceRegistrar.class)
@ComponentScan({"io.webdevice.wiring", "io.webdevice.support"})
public class WebDeviceRuntime {
}
