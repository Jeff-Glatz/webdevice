package io.automatiq.wiring;

import io.automatiq.device.LocalWebDeviceProvider;
import io.automatiq.device.RemoteWebDeviceProvider;
import io.automatiq.device.WebDeviceProvider;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

import java.io.Serializable;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static io.automatiq.driver.ConfidentialCapabilities.mark;
import static java.lang.String.format;
import static org.springframework.beans.factory.support.AbstractBeanDefinition.AUTOWIRE_BY_TYPE;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class Device
        implements Serializable {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private String name;
    private Class<? extends WebDeviceProvider<?>> provider;
    private Class<? extends WebDriver> driver;
    private URL remoteAddress;
    private Class<? extends MutableCapabilities> options;
    private String desired;
    private Map<String, Object> capabilities = new LinkedHashMap<>();
    private Map<String, Object> sauceOptions = new LinkedHashMap<>();
    private Set<String> confidential = new LinkedHashSet<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Device withName(String name) {
        setName(name);
        return this;
    }

    public Class<? extends WebDeviceProvider<?>> getProvider() {
        return provider;
    }

    public void setProvider(Class<? extends WebDeviceProvider<?>> provider) {
        this.provider = provider;
    }

    public Device withProvider(Class<? extends WebDeviceProvider<?>> provider) {
        setProvider(provider);
        return this;
    }

    public boolean isProvided() {
        return provider != null;
    }

    public Class<? extends WebDriver> getDriver() {
        return driver;
    }

    public void setDriver(Class<? extends WebDriver> driver) {
        this.driver = driver;
    }

    public Device withDriver(Class<? extends WebDriver> driver) {
        setDriver(driver);
        return this;
    }

    public URL getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(URL remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public Device withRemoteAddress(URL remoteAddress) {
        setRemoteAddress(remoteAddress);
        return this;
    }

    public boolean isRemote() {
        return remoteAddress != null;
    }

    public Class<? extends MutableCapabilities> getOptions() {
        return options;
    }

    public void setOptions(Class<? extends MutableCapabilities> options) {
        this.options = options;
    }

    public Device withOptions(Class<? extends MutableCapabilities> options) {
        setOptions(options);
        return this;
    }

    public MutableCapabilities options() {
        try {
            return options != null ?
                    options.getDeclaredConstructor().newInstance() :
                    null;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    format("Failure invoking new %s()", options.getName()), e);
        }
    }

    public String getDesired() {
        return desired;
    }

    public void setDesired(String desired) {
        this.desired = desired;
    }

    public Device withDesired(String desired) {
        setDesired(desired);
        return this;
    }

    public DesiredCapabilities desired() {
        try {
            return desired != null ?
                    (DesiredCapabilities) DesiredCapabilities.class.getDeclaredMethod(desired)
                            .invoke(DesiredCapabilities.class) :
                    null;
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    format("Failure invoking DesiredCapabilities.%s()", desired), e);
        }
    }

    public Map<String, Object> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(Map<String, Object> capabilities) {
        this.capabilities = capabilities;
    }

    public Device withCapability(String capability, Object value) {
        capabilities.put(capability, value);
        return this;
    }

    public Map<String, Object> getSauceOptions() {
        return sauceOptions;
    }

    public void setSauceOptions(Map<String, Object> sauceOptions) {
        this.sauceOptions = sauceOptions;
    }

    public Device withSauceOption(String option, Object value) {
        sauceOptions.put(option, value);
        return this;
    }

    public Set<String> getConfidential() {
        return confidential;
    }

    public void setConfidential(Set<String> confidential) {
        this.confidential = confidential;
    }

    public Device withConfidential(String mask) {
        confidential.add(mask);
        return this;
    }

    public BeanDefinitionBuilder definitionOf() {
        BeanDefinitionBuilder definition;
        if (isProvided()) {
            definition = genericBeanDefinition(provider)
                    .addConstructorArgValue(name);
        } else if (isRemote()) {
            definition = genericBeanDefinition(RemoteWebDeviceProvider.class)
                    .addConstructorArgValue(name)
                    .addConstructorArgValue(remoteAddress);
        } else {
            definition = genericBeanDefinition(LocalWebDeviceProvider.class)
                    .addConstructorArgValue(name)
                    .addConstructorArgValue(driver);
        }
        MutableCapabilities capabilities = capabilitiesOf();
        if (capabilities != null) {
            definition.addPropertyValue("capabilities", capabilities);
        }
        return definition.setAutowireMode(AUTOWIRE_BY_TYPE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(name, device.name) &&
                Objects.equals(provider, device.provider) &&
                Objects.equals(driver, device.driver) &&
                Objects.equals(remoteAddress, device.remoteAddress) &&
                Objects.equals(options, device.options) &&
                Objects.equals(desired, device.desired) &&
                Objects.equals(capabilities, device.capabilities) &&
                Objects.equals(sauceOptions, device.sauceOptions) &&
                Objects.equals(confidential, device.confidential);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, provider, driver, remoteAddress, options, desired, capabilities, sauceOptions, confidential);
    }

    private MutableCapabilities capabilitiesOf() {
        // First check for Options
        if (options != null) {
            log.info("{} capabilities will originate from {}", name, options.getName());
            MutableCapabilities options = options();
            // Merge capabilities into options
            if (!capabilities.isEmpty()) {
                log.info("Merging {} custom capabilities into options", name);
                options.merge(new DesiredCapabilities(capabilities));
            }
            // Inject sauce options
            if (!sauceOptions.isEmpty()) {
                log.info("{} capabilities will include sauce options", name);
                options.setCapability("sauce:options", new DesiredCapabilities(sauceOptions));
            }
            // Add confidential capability markers
            return mark(options, confidential);
        }
        // Next check for DesiredCapabilities
        else if (desired != null) {
            log.info("{} capabilities will originate from DesiredCapabilities.{}()", name, desired);
            DesiredCapabilities desired = desired();
            // Merge capabilities into desired
            if (!capabilities.isEmpty()) {
                log.info("Merging {} custom capabilities into desired capabilities", name);
                desired.merge(new DesiredCapabilities(capabilities));
            }
            // Inject sauce options
            if (!sauceOptions.isEmpty()) {
                log.info("{} capabilities will include sauce options", name);
                desired.setCapability("sauce:options", new DesiredCapabilities(sauceOptions));
            }
            // Add confidential capability markers
            return mark(desired, confidential);
        }
        // Then check for Capabilities
        else if (!capabilities.isEmpty()) {
            log.info("{} capabilities will originate from custom capabilities", name);
            DesiredCapabilities desired = new DesiredCapabilities(capabilities);
            // Inject sauce options
            if (!sauceOptions.isEmpty()) {
                log.info("{} capabilities will include sauce options", name);
                desired.setCapability("sauce:options", new DesiredCapabilities(sauceOptions));
            }
            // Add confidential capability markers
            return mark(desired, confidential);
        }
        // No capabilities specified
        log.info("Will not add custom capabilities to {}", name);
        return null;
    }
}
