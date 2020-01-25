package io.webdevice.support;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * There are use cases where the annotations used to configure the test context
 * will not contain the required infrastructure to support loading webdevice
 * {@link io.webdevice.wiring.Settings} from a YAML file.
 */
@Configuration("webdevice.YamlBinding")
public class YamlBinding {

    @Bean
    @ConditionalOnMissingBean
    public ApplicationConversionService conversionService() {
        return new ApplicationConversionService();
    }
}
