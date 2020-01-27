package io.webdevice.settings;

import io.webdevice.support.GenericDeviceProvider;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.support.AbstractBeanDefinition;

import static io.bestquality.util.MapBuilder.newMap;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.openqa.selenium.remote.DesiredCapabilities.iphone;
import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;
import static org.springframework.beans.factory.config.BeanDefinition.SCOPE_SINGLETON;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class ProvidedDeviceDefinitionTest
        implements DeviceDefinitionTest {
    private DeviceDefinition definition;

    @Before
    public void setUp() {
        definition = new DeviceDefinition();
    }

    // No specified capabilities

    @Test
    public void shouldBuildDefinitionWithoutCapabilitiesAndWithoutConfidential() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withProvider(GenericDeviceProvider.class)
                .build()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(GenericDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .setScope(SCOPE_SINGLETON)
                        .setRole(ROLE_INFRASTRUCTURE)
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithoutCapabilitiesAndWithConfidential() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withProvider(GenericDeviceProvider.class)
                .withConfidential("accessKey")
                .build()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(GenericDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .setScope(SCOPE_SINGLETON)
                        .setRole(ROLE_INFRASTRUCTURE)
                        .addPropertyValue("confidential", singletonList("accessKey"))
                        .getBeanDefinition());
    }

    // Capabilities originating from bean in context

    @Test
    public void shouldBuildDefinitionWithCapabilitiesReference() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withProvider(GenericDeviceProvider.class)
                .withCapabilitiesRef("myDeviceCapabilities")
                .build()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(GenericDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .setScope(SCOPE_SINGLETON)
                        .setRole(ROLE_INFRASTRUCTURE)
                        .addPropertyReference("capabilities", "myDeviceCapabilities")
                        .getBeanDefinition());
    }

    // Capabilities originating from options

    @Test
    public void shouldBuildDefinitionWithOptionsOnly() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withProvider(GenericDeviceProvider.class)
                .withOptions(MutableCapabilities.class)
                .build()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(GenericDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .setScope(SCOPE_SINGLETON)
                        .setRole(ROLE_INFRASTRUCTURE)
                        .addPropertyValue("capabilities", new MutableCapabilities())
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithOptionsMergingCapabilities() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withProvider(GenericDeviceProvider.class)
                .withOptions(MutableCapabilities.class)
                .withCapability("key", "value")
                .build()
                .getBeanDefinition();

        MutableCapabilities expectedOptions = new MutableCapabilities();
        expectedOptions.setCapability("key", "value");

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(GenericDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .setScope(SCOPE_SINGLETON)
                        .setRole(ROLE_INFRASTRUCTURE)
                        .addPropertyValue("capabilities", expectedOptions)
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithOptionsMergingExtraCapabilities() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withProvider(GenericDeviceProvider.class)
                .withOptions(MutableCapabilities.class)
                .withExtraCapability("sauce:options")
                .withExtraOption("accessKey", "2secret4u")
                .build()
                .getBeanDefinition();

        MutableCapabilities expectedOptions = new MutableCapabilities();
        expectedOptions.setCapability("sauce:options",
                new DesiredCapabilities(newMap(String.class, Object.class)
                        .with("accessKey", "2secret4u")
                        .build()));

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(GenericDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .setScope(SCOPE_SINGLETON)
                        .setRole(ROLE_INFRASTRUCTURE)
                        .addPropertyValue("capabilities", expectedOptions)
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithOptionsMergingCapabilitiesAndExtraCapabilities() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withProvider(GenericDeviceProvider.class)
                .withOptions(MutableCapabilities.class)
                .withCapability("key", "value")
                .withExtraCapability("sauce:options")
                .withExtraOption("accessKey", "2secret4u")
                .build()
                .getBeanDefinition();

        MutableCapabilities expectedOptions = new MutableCapabilities();
        expectedOptions.setCapability("key", "value");
        expectedOptions.setCapability("sauce:options",
                new DesiredCapabilities(newMap(String.class, Object.class)
                        .with("accessKey", "2secret4u")
                        .build()));

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(GenericDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .setScope(SCOPE_SINGLETON)
                        .setRole(ROLE_INFRASTRUCTURE)
                        .addPropertyValue("capabilities", expectedOptions)
                        .getBeanDefinition());
    }

    // Capabilities originating from DesiredCapabilities.xxx()

    @Test
    public void shouldBuildDefinitionWithDesiredOnly() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withProvider(GenericDeviceProvider.class)
                .withDesired("iphone")
                .build()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(GenericDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .setScope(SCOPE_SINGLETON)
                        .setRole(ROLE_INFRASTRUCTURE)
                        .addPropertyValue("capabilities", iphone())
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithDesiredMergingCapabilities() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withProvider(GenericDeviceProvider.class)
                .withDesired("iphone")
                .withCapability("key", "value")
                .build()
                .getBeanDefinition();

        DesiredCapabilities expectedCapabilities = iphone();
        expectedCapabilities.setCapability("key", "value");

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(GenericDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .setScope(SCOPE_SINGLETON)
                        .setRole(ROLE_INFRASTRUCTURE)
                        .addPropertyValue("capabilities", expectedCapabilities)
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithDesiredMergingExtraCapabilities() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withProvider(GenericDeviceProvider.class)
                .withDesired("iphone")
                .withExtraCapability("sauce:options")
                .withExtraOption("accessKey", "2secret4u")
                .build()
                .getBeanDefinition();

        DesiredCapabilities expectedCapabilities = iphone();
        expectedCapabilities.setCapability("sauce:options",
                new DesiredCapabilities(newMap(String.class, Object.class)
                        .with("accessKey", "2secret4u")
                        .build()));

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(GenericDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .setScope(SCOPE_SINGLETON)
                        .setRole(ROLE_INFRASTRUCTURE)
                        .addPropertyValue("capabilities", expectedCapabilities)
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithDesiredMergingCapabilitiesAndExtraCapabilities() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withProvider(GenericDeviceProvider.class)
                .withDesired("iphone")
                .withCapability("key", "value")
                .withExtraCapability("sauce:options")
                .withExtraOption("accessKey", "2secret4u")
                .build()
                .getBeanDefinition();

        DesiredCapabilities expectedCapabilities = iphone();
        expectedCapabilities.setCapability("key", "value");
        expectedCapabilities.setCapability("sauce:options",
                new DesiredCapabilities(newMap(String.class, Object.class)
                        .with("accessKey", "2secret4u")
                        .build()));

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(GenericDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .setScope(SCOPE_SINGLETON)
                        .setRole(ROLE_INFRASTRUCTURE)
                        .addPropertyValue("capabilities", expectedCapabilities)
                        .getBeanDefinition());
    }


    // Capabilities originating from Map

    @Test
    public void shouldBuildDefinitionWithMapOnly() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withProvider(GenericDeviceProvider.class)
                .withCapability("key", "value")
                .build()
                .getBeanDefinition();

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(GenericDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .setScope(SCOPE_SINGLETON)
                        .setRole(ROLE_INFRASTRUCTURE)
                        .addPropertyValue("capabilities", new DesiredCapabilities(newMap(String.class, Object.class)
                                .with("key", "value")
                                .build()))
                        .getBeanDefinition());
    }

    @Test
    public void shouldBuildDefinitionWithMapMergingExtraCapabilities() {
        AbstractBeanDefinition actual = definition.withName("myDevice")
                .withProvider(GenericDeviceProvider.class)
                .withCapability("key", "value")
                .withExtraCapability("sauce:options")
                .withExtraOption("accessKey", "2secret4u")
                .build()
                .getBeanDefinition();

        DesiredCapabilities expectedCapabilities = new DesiredCapabilities(newMap(String.class, Object.class)
                .with("key", "value")
                .build());
        expectedCapabilities.setCapability("sauce:options",
                new DesiredCapabilities(newMap(String.class, Object.class)
                        .with("accessKey", "2secret4u")
                        .build()));

        assertThat(actual)
                .isEqualTo(genericBeanDefinition(GenericDeviceProvider.class)
                        .addConstructorArgValue("myDevice")
                        .setScope(SCOPE_SINGLETON)
                        .setRole(ROLE_INFRASTRUCTURE)
                        .addPropertyValue("capabilities", expectedCapabilities)
                        .getBeanDefinition());
    }
}
