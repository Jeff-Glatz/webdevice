package io.webdevice.wiring;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

import static org.assertj.core.api.Assertions.assertThat;

public class DeviceSettingsTest {
    private DeviceSettings settings;

    @Before
    public void setUp() {
        settings = new DeviceSettings();
    }

    @Test
    public void shouldBuildProvidedDeviceDefinition() {
        AbstractBeanDefinition definition = settings.withName("myDevice")
                .withProvider(SameDeviceProvider.class)
                .definitionOf()
                .getBeanDefinition();

        ConstructorArgumentValues argumentValues = definition.getConstructorArgumentValues();
        assertThat(argumentValues.getArgumentCount())
                .isEqualTo(1);
        assertThat(argumentValues.hasIndexedArgumentValue(0)).isTrue();
        assertThat(argumentValues.getIndexedArgumentValue(0, String.class).getValue())
                .isEqualTo("myDevice");
    }
}
