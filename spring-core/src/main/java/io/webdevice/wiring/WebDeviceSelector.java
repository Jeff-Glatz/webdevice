package io.webdevice.wiring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

import static io.webdevice.wiring.Settings.scope;

public class WebDeviceSelector
        implements ImportSelector {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<String, String> scopeToImport = new HashMap<>();
    private final Environment environment;

    @Autowired
    public WebDeviceSelector(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void initialize() {
        scopeToImport.put(null, "io.webdevice.wiring.DefaultScopeWiring");
        scopeToImport.put("", "io.webdevice.wiring.DefaultScopeWiring");
        scopeToImport.put("singleton", "io.webdevice.wiring.DefaultScopeWiring");
        scopeToImport.put("application", "io.webdevice.wiring.DefaultScopeWiring");
        scopeToImport.put("webdevice", "io.webdevice.wiring.WebDeviceScopeWiring");
        scopeToImport.put("cucumber-glue", "io.webdevice.wiring.CucumberGlueScopeWiring");
    }

    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        String configuration = scopeToImport.get(environment.getProperty("webdevice.scope", scope()));
        log.info("Importing wiring from {}", configuration);
        return new String[]{configuration};
    }
}
