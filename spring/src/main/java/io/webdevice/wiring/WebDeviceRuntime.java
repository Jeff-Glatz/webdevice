package io.webdevice.wiring;

import io.webdevice.device.DeviceRegistry;
import io.webdevice.device.WebDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Configuration
@Import(DeviceRegistrar.class)
@ComponentScan({"io.webdevice.support", "io.webdevice.wiring"})
@EnableConfigurationProperties(Settings.class)
public class WebDeviceRuntime {
    private final Settings settings;

    @Autowired
    public WebDeviceRuntime(Settings settings) {
        this.settings = settings;
    }

    @Primary
    @Scope(SCOPE_CUCUMBER_GLUE)
    @Bean(initMethod = "initialize", destroyMethod = "release")
    public WebDevice webDevice(DeviceRegistry registry) {
        return new WebDevice(registry)
                .withBaseUrl(settings.getBaseUrl())
                .withDefaultDevice(settings.getDefaultDevice())
                .withEager(settings.isEager())
                .withStrict(settings.isStrict());
    }
}
