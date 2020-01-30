package io.webdevice.settings;

import org.junit.Before;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.convert.support.ConfigurableConversionService;

import static io.bestquality.util.MapBuilder.mapOf;

public abstract class ConfigurationPropertiesTest
        extends BoundSettingsTest {

    @Before
    public void exportBinder() {
        environmentWith(mapOf(String.class, Object.class)
                .with("webdevice.binder", ConfigurationPropertiesBinder.class.getName())
                .build());
    }

    @Override
    protected SettingsBinder makeSettingsBinder() {
        return new ConfigurationPropertiesBinder();
    }

    @Override
    protected ConfigurableConversionService makeConversionService() {
        return new ApplicationConversionService();
    }
}
