package automaton.wiring;

import automaton.device.LocalWebDeviceProvider;
import automaton.device.RemoteWebDeviceProvider;
import automaton.device.WebDeviceProvider;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

import java.io.Serializable;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static org.springframework.beans.factory.support.AbstractBeanDefinition.AUTOWIRE_BY_TYPE;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.genericBeanDefinition;

public class Device
        implements Serializable {
    private String name;
    private Class<? extends WebDeviceProvider<?>> provider;
    private Class<? extends WebDriver> driver;
    private URL remoteAddress;
    private Map<String, Object> capabilities = new LinkedHashMap<>();

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

    public boolean isCustomized() {
        return !capabilities.isEmpty();
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
        if (isCustomized()) {
            definition.addPropertyValue("capabilities", new DesiredCapabilities(capabilities));
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
                Objects.equals(capabilities, device.capabilities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, provider, driver, remoteAddress, capabilities);
    }
}
