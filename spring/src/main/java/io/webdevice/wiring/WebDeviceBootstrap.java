package io.webdevice.wiring;

import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * This class exists to properly order the configurations when the WebDevice runtime
 * is activated with the {@link EnableWebDevice} annotation. The {@link SettingsExporter}
 * must always be processed before the {@link WebDeviceRuntime} so that settings
 * overrides will be exported to the {@link org.springframework.core.env.ConfigurableEnvironment}
 * before they are bound to the {@link io.webdevice.settings.Settings} by {@link WebDeviceRuntime}
 *
 * @see EnableWebDevice
 * @see SettingsExporter
 * @see WebDeviceRuntime
 */
public class WebDeviceBootstrap
        implements DeferredImportSelector {

    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        return new String[]{SettingsExporter.class.getName(),
                WebDeviceRuntime.class.getName()};
    }
}
