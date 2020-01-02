package io.webdevice.wiring;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(DeviceRegistrar.class)
@ComponentScan("io.webdevice.device")
@EnableConfigurationProperties(Settings.class)
public class WebDeviceRuntime {
}
