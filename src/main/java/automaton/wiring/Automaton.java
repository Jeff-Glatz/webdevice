package automaton.wiring;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.net.URL;

@Configuration
@Import(DeviceRegistrar.class)
@ComponentScan("automaton.device")
@EnableConfigurationProperties
public class Automaton {

    @Bean
    @ConfigurationProperties("automaton")
    public Settings settings() {
        return new Settings();
    }

    @Bean
    @ConditionalOnMissingBean(name = "baseUrl", value = URL.class)
    public URL baseUrl(Settings settings) {
        return settings.getBaseUrl();
    }
}
