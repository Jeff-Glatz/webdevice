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
    private boolean pooled = true;
    private Class<? extends WebDeviceProvider<?>> provider;
    private Class<? extends WebDriver> driver;
    private URL remoteAddress;
    private Class<? extends MutableCapabilities> options;
    private String desired;
    private Map<String, Object> capabilities = new LinkedHashMap<>();
    private String extraCapability;
    private Map<String, Object> extraOptions = new LinkedHashMap<>();
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

    public boolean isPooled() {
        return pooled;
    }

    public void setPooled(boolean pooled) {
        this.pooled = pooled;
    }

    public Device withPooled(boolean pooled) {
        setPooled(pooled);
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

    public String getExtraCapability() {
        return extraCapability;
    }

    public void setExtraCapability(String extraCapability) {
        this.extraCapability = extraCapability;
    }

    public Device withExtraCapability(String extraCapability) {
        this.extraCapability = extraCapability;
        return this;
    }

    public Map<String, Object> getExtraOptions() {
        return extraOptions;
    }

    public void setExtraOptions(Map<String, Object> extraOptions) {
        this.extraOptions = extraOptions;
    }

    public Device withExtraOption(String option, Object value) {
        extraOptions.put(option, value);
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
        return pooled == device.pooled &&
                Objects.equals(name, device.name) &&
                Objects.equals(provider, device.provider) &&
                Objects.equals(driver, device.driver) &&
                Objects.equals(remoteAddress, device.remoteAddress) &&
                Objects.equals(options, device.options) &&
                Objects.equals(desired, device.desired) &&
                Objects.equals(capabilities, device.capabilities) &&
                Objects.equals(extraCapability, device.extraCapability) &&
                Objects.equals(extraOptions, device.extraOptions) &&
                Objects.equals(confidential, device.confidential);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, pooled, provider, driver, remoteAddress, options, desired, capabilities, extraCapability, extraOptions, confidential);
    }

    private MutableCapabilities withCapabilities(MutableCapabilities capabilities) {
        if (!this.capabilities.isEmpty()) {
            log.info("Merging {} custom capabilities", name);
            capabilities.merge(new DesiredCapabilities(this.capabilities));
        }
        return capabilities;
    }

    private MutableCapabilities withExtraCapability(MutableCapabilities capabilities) {
        if (!extraOptions.isEmpty()) {
            log.info("{} capabilities will include {}", name, extraCapability);
            capabilities.setCapability(extraCapability, new DesiredCapabilities(extraOptions));
        }
        return capabilities;
    }

    private MutableCapabilities fromOptions() {
        log.info("{} capabilities will originate from {}", name, options.getName());
        return mark(withExtraCapability(withCapabilities(options())), confidential);
    }

    private MutableCapabilities fromDesiredCapabilities() {
        log.info("{} capabilities will originate from DesiredCapabilities.{}()", name, desired);
        return mark(withExtraCapability(withCapabilities(desired())), confidential);
    }

    private MutableCapabilities fromCapabilities() {
        log.info("{} capabilities will originate from custom capabilities", name);
        return mark(withExtraCapability(new DesiredCapabilities(capabilities)), confidential);
    }

    private MutableCapabilities capabilitiesOf() {
        if (options != null) {
            return fromOptions();
        } else if (desired != null) {
            return fromDesiredCapabilities();
        } else if (!capabilities.isEmpty()) {
            return fromCapabilities();
        }
        log.info("Will not add custom capabilities to {}", name);
        return null;
    }
}
