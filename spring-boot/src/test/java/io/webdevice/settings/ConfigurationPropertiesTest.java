package io.webdevice.settings;

import io.webdevice.support.YamlPropertySourceFactory;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.io.support.PropertySourceFactory;

public abstract class ConfigurationPropertiesTest
        extends BoundSettingsTest {

    @Override
    protected SettingsBinder makeSettingsBinder() {
        return new ConfigurationPropertiesBinder();
    }

    @Override
    protected ConfigurableConversionService makeConversionService() {
        return new ApplicationConversionService();
    }

    @Override
    protected PropertySourceFactory makePropertySourceFactory() {
        return new YamlPropertySourceFactory();
    }
}
