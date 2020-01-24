package io.webdevice.wiring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(WebDeviceRegistrar.class)
public class WebDeviceRuntime {

    @Bean
    @ConditionalOnMissingBean
    public ApplicationConversionService conversionService() {
        return new ApplicationConversionService();
    }

    @Bean("webdevice.WebDeviceDependsOn")
    public WebDeviceDependsOn dependsOn() {
        return new WebDeviceDependsOn();
    }
}
