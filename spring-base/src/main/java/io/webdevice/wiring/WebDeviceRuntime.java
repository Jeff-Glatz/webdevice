package io.webdevice.wiring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(WebDeviceRegistrar.class)
@Configuration("webdevice.WebDeviceRuntime")
@ComponentScan({"io.webdevice.wiring", "io.webdevice.support"})
public class WebDeviceRuntime {
}
