package io.webdevice.support;

import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Bean;

public class YamlBinding {

    @Bean
    public ApplicationConversionService conversionService() {
        return new ApplicationConversionService();
    }
}
