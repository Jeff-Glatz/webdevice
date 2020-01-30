package io.webdevice.configurations;

import io.webdevice.wiring.EnableWebDevice;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableWebDevice(settings = "io/webdevice/wiring/direct-pooled-device.yaml")
public class DirectPooledDevice {
}
