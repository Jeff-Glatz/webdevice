package io.webdevice.wiring;

import io.webdevice.device.Browser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Scope;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Configuration
@Import(DeviceRegistrar.class)
@EnableConfigurationProperties(Settings.class)
public class WebDeviceWiring {
    private final ApplicationContext context;
    private final Settings settings;

    @Autowired
    public WebDeviceWiring(ApplicationContext context, Settings settings) {
        this.context = context;
        this.settings = settings;
    }

    @Bean
    @Scope(SCOPE_CUCUMBER_GLUE)
    public Browser browser() {
        return new Browser(context, settings.getDefaultDevice())
                .withBaseUrl(settings.getBaseUrl());
    }
}
