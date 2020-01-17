package io.webdevice.wiring;

import io.webdevice.device.RemoteDeviceProvider;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

import java.net.URL;

import static io.webdevice.util.Collections.mapOf;
import static io.webdevice.util.Collections.setOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.remote.DesiredCapabilities.iphone;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class RemoteDeviceDefinitionTest
        implements DeviceDefinitionTest {
    private DeviceMetadata metadata;
    private URL remoteAddress;

    @Before
    public void setUp()
            throws Exception {
        metadata = new DeviceMetadata();
        remoteAddress = new URL("http://webdevice.io");
    }

    // No specified capabilities

    @Test
    public void shouldBuildDefinitionWithoutCapabilitiesAndWithoutConfidential() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withRemoteAddress(remoteAddress)
                .buildDefinition()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(RemoteDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(remoteAddress)
                        .setInitMethodName("initialize")
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithoutCapabilitiesAndWithConfidential() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withRemoteAddress(remoteAddress)
                .withConfidential("accessKey")
                .buildDefinition()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(RemoteDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(remoteAddress)
                        .setInitMethodName("initialize")
                        .addPropertyValue("confidential", setOf("accessKey"))
                        .getBeanDefinition());
    }

    // Capabilities originating from options

    @Test
    public void shouldBuildDefinitionWithOptionsOnly() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withRemoteAddress(remoteAddress)
                .withOptions(FirefoxOptions.class)
                .buildDefinition()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(RemoteDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(remoteAddress)
                        .setInitMethodName("initialize")
                        .addPropertyValue("capabilities", new FirefoxOptions())
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithOptionsMergingCapabilities() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withRemoteAddress(remoteAddress)
                .withOptions(FirefoxOptions.class)
                .withCapability("key", "value")
                .buildDefinition()
                .getBeanDefinition();

        FirefoxOptions expectedOptions = new FirefoxOptions();
        expectedOptions.setCapability("key", "value");

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(RemoteDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(remoteAddress)
                        .setInitMethodName("initialize")
                        .addPropertyValue("capabilities", expectedOptions)
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithOptionsMergingExtraCapabilities() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withRemoteAddress(remoteAddress)
                .withOptions(FirefoxOptions.class)
                .withExtraCapability("sauce:options")
                .withExtraOption("accessKey", "2secret4u")
                .buildDefinition()
                .getBeanDefinition();

        FirefoxOptions expectedOptions = new FirefoxOptions();
        expectedOptions.setCapability("sauce:options",
                new DesiredCapabilities(mapOf("accessKey", "2secret4u")));

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(RemoteDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(remoteAddress)
                        .setInitMethodName("initialize")
                        .addPropertyValue("capabilities", expectedOptions)
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithOptionsMergingCapabilitiesAndExtraCapabilities() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withRemoteAddress(remoteAddress)
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
                .isEqualTo(genericBeanDefinition(RemoteDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(remoteAddress)
                        .setInitMethodName("initialize")
                        .addPropertyValue("capabilities", expectedOptions)
                        .getBeanDefinition());
    }

    // Capabilities originating from DesiredCapabilities.xxx()

    @Test
    public void shouldBuildDefinitionWithDesiredOnly() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withRemoteAddress(remoteAddress)
                .withDesired("iphone")
                .buildDefinition()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(RemoteDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(remoteAddress)
                        .setInitMethodName("initialize")
                        .addPropertyValue("capabilities", iphone())
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithDesiredMergingCapabilities() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withRemoteAddress(remoteAddress)
                .withDesired("iphone")
                .withCapability("key", "value")
                .buildDefinition()
                .getBeanDefinition();

        DesiredCapabilities expectedCapabilities = iphone();
        expectedCapabilities.setCapability("key", "value");

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(RemoteDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(remoteAddress)
                        .setInitMethodName("initialize")
                        .addPropertyValue("capabilities", expectedCapabilities)
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithDesiredMergingExtraCapabilities() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withRemoteAddress(remoteAddress)
                .withDesired("iphone")
                .withExtraCapability("sauce:options")
                .withExtraOption("accessKey", "2secret4u")
                .buildDefinition()
                .getBeanDefinition();

        DesiredCapabilities expectedCapabilities = iphone();
        expectedCapabilities.setCapability("sauce:options",
                new DesiredCapabilities(mapOf("accessKey", "2secret4u")));

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(RemoteDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(remoteAddress)
                        .setInitMethodName("initialize")
                        .addPropertyValue("capabilities", expectedCapabilities)
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithDesiredMergingCapabilitiesAndExtraCapabilities() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withRemoteAddress(remoteAddress)
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
                .isEqualTo(genericBeanDefinition(RemoteDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(remoteAddress)
                        .setInitMethodName("initialize")
                        .addPropertyValue("capabilities", expectedCapabilities)
                        .getBeanDefinition());
    }


    // Capabilities originating from Map

    @Test
    public void shouldBuildDefinitionWithMapOnly() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withRemoteAddress(remoteAddress)
                .withCapability("key", "value")
                .buildDefinition()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(RemoteDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(remoteAddress)
                        .setInitMethodName("initialize")
                        .addPropertyValue("capabilities", new DesiredCapabilities(mapOf("key", "value")))
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithMapMergingExtraCapabilities() {
        AbstractBeanDefinition actual = metadata.withName("myDevice")
                .withRemoteAddress(remoteAddress)
                .withCapability("key", "value")
                .withExtraCapability("sauce:options")
                .withExtraOption("accessKey", "2secret4u")
                .buildDefinition()
                .getBeanDefinition();

        DesiredCapabilities expectedCapabilities = new DesiredCapabilities(mapOf("key", "value"));
        expectedCapabilities.setCapability("sauce:options",
                new DesiredCapabilities(mapOf("accessKey", "2secret4u")));

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(RemoteDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .addConstructorArgValue(remoteAddress)
                        .setInitMethodName("initialize")
                        .addPropertyValue("capabilities", expectedCapabilities)
                        .getBeanDefinition());
    }
}
