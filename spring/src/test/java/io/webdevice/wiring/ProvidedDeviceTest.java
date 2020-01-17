package io.webdevice.wiring;

import io.webdevice.support.CustomFirefoxProvider;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

import static io.webdevice.util.Collections.mapOf;
import static io.webdevice.util.Collections.setOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class ProvidedDeviceTest {
    private DeviceMetadata metadata;

    @Before
    public void setUp() {
        metadata = new DeviceMetadata();
    }

    // No specified capabilities

    @Test
    public void shouldBuildDefinitionWithoutCapabilitiesAndWithoutConfidential() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withProvider(CustomFirefoxProvider.class)
                .buildDefinition()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(CustomFirefoxProvider.class)
                        .addConstructorArgValue("myDevice")
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithoutCapabilitiesAndWithConfidential() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withProvider(CustomFirefoxProvider.class)
                .withConfidential("accessKey")
                .buildDefinition()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(CustomFirefoxProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addPropertyValue("confidential", setOf("accessKey"))
                        .getBeanDefinition());
    }

    // Capabilities originating from options

    @Test
    public void shouldBuildDefinitionWithOptionsOnly() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withProvider(CustomFirefoxProvider.class)
                .withOptions(FirefoxOptions.class)
                .buildDefinition()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(CustomFirefoxProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addPropertyValue("capabilities", new FirefoxOptions())
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithOptionsMergingCapabilities() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withProvider(CustomFirefoxProvider.class)
                .withOptions(FirefoxOptions.class)
                .withCapability("key", "value")
                .buildDefinition()
                .getBeanDefinition();

        FirefoxOptions expectedOptions = new FirefoxOptions();
        expectedOptions.setCapability("key", "value");

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(CustomFirefoxProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addPropertyValue("capabilities", expectedOptions)
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithOptionsMergingExtraCapabilities() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withProvider(CustomFirefoxProvider.class)
                .withOptions(FirefoxOptions.class)
                .withExtraCapability("sauce:options")
                .withExtraOption("accessKey", "2secret4u")
                .buildDefinition()
                .getBeanDefinition();

        FirefoxOptions expectedOptions = new FirefoxOptions();
        expectedOptions.setCapability("sauce:options",
                new DesiredCapabilities(mapOf("accessKey", "2secret4u")));

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(CustomFirefoxProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addPropertyValue("capabilities", expectedOptions)
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithOptionsMergingCapabilitiesAndExtraCapabilities() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withProvider(CustomFirefoxProvider.class)
                .withOptions(FirefoxOptions.class)
                .withCapability("key", "value")
                .withExtraCapability("sauce:options")
                .withExtraOption("accessKey", "2secret4u")
                .buildDefinition()
                .getBeanDefinition();

        FirefoxOptions expectedOptions = new FirefoxOptions();
        expectedOptions.setCapability("key", "value");
        expectedOptions.setCapability("sauce:options",
                new DesiredCapabilities(mapOf("accessKey", "2secret4u")));

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(CustomFirefoxProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addPropertyValue("capabilities", expectedOptions)
                        .getBeanDefinition());
    }

    // Capabilities originating from DesiredCapabilities.xxx()

    // Capabilities originating from Map
}
