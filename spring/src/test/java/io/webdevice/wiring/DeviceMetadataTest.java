package io.webdevice.wiring;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

import static org.assertj.core.api.Assertions.assertThat;

public class DeviceMetadataTest {
    private DeviceMetadata metadata;

    @Before
    public void setUp() {
        metadata = new DeviceMetadata();
    }

    @Test
    public void shouldBuildProvidedDeviceDefinition() {
        AbstractBeanDefinition definition = metadata.withName("myDevice")
                .withProvider(SameDeviceProvider.class)
                .buildDefinition()
                .getBeanDefinition();

        ConstructorArgumentValues argumentValues = definition.getConstructorArgumentValues();
        assertThat(argumentValues.getArgumentCount())
                .isEqualTo(1);
        assertThat(argumentValues.hasIndexedArgumentValue(0)).isTrue();
        assertThat(argumentValues.getIndexedArgumentValue(0, String.class).getValue())
                .isEqualTo("myDevice");
    }

    @Test
    public void twoProvidedDevicesShouldBeEqual() {
        metadata.withName("myDevice")
                .withProvider(SameDeviceProvider.class);

        DeviceMetadata metadata2 = new DeviceMetadata()
                .withName("myDevice")
                .withProvider(SameDeviceProvider.class);

        assertThat(metadata2)
                .isEqualTo(metadata);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseExceptionWhenDesiredCapabilitiesFactoryMethodDoesNotExist() {
        metadata.withName("myDevice")
                .withDriver(FirefoxDriver.class)
                .withDesired("doesNotExist")
                .buildDefinition();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseExceptionWhenFailureOccursConstructingOptions() {
        metadata.withName("myDevice")
                .withDriver(FirefoxDriver.class)
                .withOptions(BadOptions.class)
                .buildDefinition();
    }

    public static class BadOptions
            extends FirefoxOptions {
        private BadOptions() {
        }
    }
}
