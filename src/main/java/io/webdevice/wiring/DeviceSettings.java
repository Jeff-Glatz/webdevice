package io.webdevice.wiring;

import io.webdevice.device.RemoteWebDeviceProvider;
import io.webdevice.device.LocalWebDeviceProvider;
import io.webdevice.device.WebDeviceProvider;
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
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableMap;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.hash;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class DeviceSettings
        implements Serializable {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Set<String> aliases = new LinkedHashSet<>();
    private final Map<String, Object> capabilities = new LinkedHashMap<>();
    private final Map<String, Object> extraOptions = new LinkedHashMap<>();
    private final Set<String> confidential = new LinkedHashSet<>();
    private String name;
    private boolean pooled = true;
    private Class<? extends WebDeviceProvider> provider;
    private Class<? extends WebDriver> driver;
    private URL remoteAddress;
    private String capabilitiesRef;
    private Class<? extends MutableCapabilities> options;
    private String desired;
    private String extraCapability;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DeviceSettings withName(String name) {
        setName(name);
        return this;
    }

    public Set<String> getAliases() {
        return unmodifiableSet(aliases);
    }

    public void setAliases(Set<String> aliases) {
        this.aliases.clear();
        this.aliases.addAll(aliases);
    }

    public DeviceSettings withAlias(String alias) {
        aliases.add(alias);
        return this;
    }

    public Stream<String> aliases() {
        return aliases.stream();
    }

    public boolean isPooled() {
        return pooled;
    }

    public void setPooled(boolean pooled) {
        this.pooled = pooled;
    }

    public DeviceSettings withPooled(boolean pooled) {
        setPooled(pooled);
        return this;
    }

    public Class<? extends WebDeviceProvider> getProvider() {
        return provider;
    }

    public void setProvider(Class<? extends WebDeviceProvider> provider) {
        this.provider = provider;
    }

    public DeviceSettings withProvider(Class<? extends WebDeviceProvider> provider) {
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

    public DeviceSettings withDriver(Class<? extends WebDriver> driver) {
        setDriver(driver);
        return this;
    }

    public URL getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(URL remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public DeviceSettings withRemoteAddress(URL remoteAddress) {
        setRemoteAddress(remoteAddress);
        return this;
    }

    public boolean isRemote() {
        return remoteAddress != null;
    }

    public String getCapabilitiesRef() {
        return capabilitiesRef;
    }

    public void setCapabilitiesRef(String capabilitiesRef) {
        this.capabilitiesRef = capabilitiesRef;
    }

    public DeviceSettings withCapabilitiesRef(String capabilitiesRef) {
        setCapabilitiesRef(capabilitiesRef);
        return this;
    }

    public Class<? extends MutableCapabilities> getOptions() {
        return options;
    }

    public void setOptions(Class<? extends MutableCapabilities> options) {
        this.options = options;
    }

    public DeviceSettings withOptions(Class<? extends MutableCapabilities> options) {
        setOptions(options);
        return this;
    }

    public String getDesired() {
        return desired;
    }

    public void setDesired(String desired) {
        this.desired = desired;
    }

    public DeviceSettings withDesired(String desired) {
        setDesired(desired);
        return this;
    }

    public Map<String, Object> getCapabilities() {
        return unmodifiableMap(capabilities);
    }

    public void setCapabilities(Map<String, Object> capabilities) {
        this.capabilities.clear();
        this.capabilities.putAll(capabilities);
    }

    public DeviceSettings withCapability(String capability, Object value) {
        capabilities.put(capability, value);
        return this;
    }

    public String getExtraCapability() {
        return extraCapability;
    }

    public void setExtraCapability(String extraCapability) {
        this.extraCapability = extraCapability;
    }

    public DeviceSettings withExtraCapability(String extraCapability) {
        this.extraCapability = extraCapability;
        return this;
    }

    public Map<String, Object> getExtraOptions() {
        return unmodifiableMap(extraOptions);
    }

    public void setExtraOptions(Map<String, Object> extraOptions) {
        this.extraOptions.clear();
        this.extraOptions.putAll(extraOptions);
    }

    public DeviceSettings withExtraOption(String option, Object value) {
        extraOptions.put(option, value);
        return this;
    }

    public Set<String> getConfidential() {
        return unmodifiableSet(confidential);
    }

    public void setConfidential(Set<String> confidential) {
        this.confidential.clear();
        this.confidential.addAll(confidential);
    }

    public DeviceSettings withConfidential(String mask) {
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
                    .addConstructorArgValue(remoteAddress)
                    .addConstructorArgValue(confidential);
        } else {
            definition = genericBeanDefinition(LocalWebDeviceProvider.class)
                    .addConstructorArgValue(name)
                    .addConstructorArgValue(driver);
        }
        return addCapabilities(definition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceSettings device = (DeviceSettings) o;
        return pooled == device.pooled &&
                Objects.equals(name, device.name) &&
                Objects.equals(aliases, device.aliases) &&
                Objects.equals(provider, device.provider) &&
                Objects.equals(driver, device.driver) &&
                Objects.equals(remoteAddress, device.remoteAddress) &&
                Objects.equals(capabilitiesRef, device.capabilitiesRef) &&
                Objects.equals(options, device.options) &&
                Objects.equals(desired, device.desired) &&
                Objects.equals(capabilities, device.capabilities) &&
                Objects.equals(extraCapability, device.extraCapability) &&
                Objects.equals(extraOptions, device.extraOptions) &&
                Objects.equals(confidential, device.confidential);
    }

    @Override
    public int hashCode() {
        return hash(name, aliases, pooled, provider, driver,
                remoteAddress, capabilitiesRef, options, desired,
                capabilities, extraCapability, extraOptions, confidential);
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

    private MutableCapabilities options() {
        try {
            return options.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    format("Failure invoking new %s()", options.getName()), e);
        }
    }

    private MutableCapabilities fromOptions() {
        log.info("{} capabilities will originate from {}", name, options.getName());
        return withExtraCapability(withCapabilities(options()));
    }

    private DesiredCapabilities desired() {
        try {
            return (DesiredCapabilities) DesiredCapabilities.class
                    .getDeclaredMethod(desired)
                    .invoke(DesiredCapabilities.class);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    format("Failure invoking DesiredCapabilities.%s()", desired), e);
        }
    }

    private MutableCapabilities fromDesiredCapabilities() {
        log.info("{} capabilities will originate from DesiredCapabilities.{}()", name, desired);
        return withExtraCapability(withCapabilities(desired()));
    }

    private MutableCapabilities fromCapabilities() {
        log.info("{} capabilities will originate from custom capabilities", name);
        return withExtraCapability(new DesiredCapabilities(capabilities));
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

    private BeanDefinitionBuilder addCapabilities(BeanDefinitionBuilder definition) {
        MutableCapabilities capabilities = capabilitiesOf();
        if (capabilities != null) {
            log.info("{} adding computed capabilities to BeanDefinition", name);
            definition.addPropertyValue("capabilities", capabilities);
        } else if (capabilitiesRef != null) {
            log.info("{} adding capabilities reference to BeanDefinition", name);
            definition.addPropertyReference("capabilities", capabilitiesRef);
        }
        return definition;
    }
}
