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
import static org.openqa.selenium.remote.DesiredCapabilities.iphone;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class ProvidedDeviceDefinitionTest
        implements DeviceDefinitionTest {
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

    // Capabilities originating from bean in context

    @Test
    public void shouldBuildDefinitionWithCapabilitiesReference() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withProvider(CustomFirefoxProvider.class)
                .withCapabilitiesRef("myDeviceCapabilities")
                .buildDefinition()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(CustomFirefoxProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addPropertyReference("capabilities", "myDeviceCapabilities")
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

    @Test
    public void shouldBuildDefinitionWithDesiredOnly() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withProvider(CustomFirefoxProvider.class)
                .withDesired("iphone")
                .buildDefinition()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(CustomFirefoxProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addPropertyValue("capabilities", iphone())
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithDesiredMergingCapabilities() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withProvider(CustomFirefoxProvider.class)
                .withDesired("iphone")
                .withCapability("key", "value")
                .buildDefinition()
                .getBeanDefinition();

        DesiredCapabilities expectedCapabilities = iphone();
        expectedCapabilities.setCapability("key", "value");

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(CustomFirefoxProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addPropertyValue("capabilities", expectedCapabilities)
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithDesiredMergingExtraCapabilities() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withProvider(CustomFirefoxProvider.class)
                .withDesired("iphone")
                .withExtraCapability("sauce:options")
                .withExtraOption("accessKey", "2secret4u")
                .buildDefinition()
                .getBeanDefinition();

        DesiredCapabilities expectedCapabilities = iphone();
        expectedCapabilities.setCapability("sauce:options",
                new DesiredCapabilities(mapOf("accessKey", "2secret4u")));

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(CustomFirefoxProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addPropertyValue("capabilities", expectedCapabilities)
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithDesiredMergingCapabilitiesAndExtraCapabilities() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withProvider(CustomFirefoxProvider.class)
                .withDesired("iphone")
                .withCapability("key", "value")
                .withExtraCapability("sauce:options")
                .withExtraOption("accessKey", "2secret4u")
                .buildDefinition()
                .getBeanDefinition();

        DesiredCapabilities expectedCapabilities = iphone();
        expectedCapabilities.setCapability("key", "value");
        expectedCapabilities.setCapability("sauce:options",
                new DesiredCapabilities(mapOf("accessKey", "2secret4u")));

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(CustomFirefoxProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addPropertyValue("capabilities", expectedCapabilities)
                        .getBeanDefinition());
    }


    // Capabilities originating from Map

    @Test
    public void shouldBuildDefinitionWithMapOnly() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withProvider(CustomFirefoxProvider.class)
                .withCapability("key", "value")
                .buildDefinition()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(CustomFirefoxProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addPropertyValue("capabilities", new DesiredCapabilities(mapOf("key", "value")))
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithMapMergingExtraCapabilities() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withProvider(CustomFirefoxProvider.class)
                .withCapability("key", "value")
                .withExtraCapability("sauce:options")
                .withExtraOption("accessKey", "2secret4u")
                .buildDefinition()
                .getBeanDefinition();

        DesiredCapabilities expectedCapabilities = new DesiredCapabilities(mapOf("key", "value"));
        expectedCapabilities.setCapability("sauce:options",
                new DesiredCapabilities(mapOf("accessKey", "2secret4u")));

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(CustomFirefoxProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addPropertyValue("capabilities", expectedCapabilities)
                        .getBeanDefinition());
    }
}
