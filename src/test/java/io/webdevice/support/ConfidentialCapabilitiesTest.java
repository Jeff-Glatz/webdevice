package io.webdevice.support;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.MutableCapabilities;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class ConfidentialCapabilitiesTest {
    private Set<String> confidential;
    private ProtectedCapabilities capabilities;

    @Before
    public void setUp() {
        confidential = new LinkedHashSet<>();
        capabilities = new ProtectedCapabilities(new MutableCapabilities(), confidential);
    }

    @Test
    public void shouldNotMaskCapability() {
        capabilities.setCapability("accessKey", "2secret4u");

        assertThat(capabilities.toString())
                .isEqualTo("{accessKey: 2secret4u}");
    }

    @Test
    public void shouldMaskConfidentialCapability() {
        confidential.add("accessKey");
        capabilities.setCapability("accessKey", "2secret4u");

        assertThat(capabilities.toString())
                .isEqualTo("{accessKey: ********}");
    }

    @Test
    public void shouldNotMaskNestedConfidentialCapability() {
        MutableCapabilities sauceOptions = new MutableCapabilities();
        sauceOptions.setCapability("accessKey", "2secret4u");

        capabilities.setCapability("sauce:options", sauceOptions);

        assertThat(capabilities.toString())
                .isEqualTo("{sauce:options: {accessKey: 2secret4u}}");
    }

    @Test
    public void shouldMaskNestedConfidentialCapability() {
        confidential.add("accessKey");

        MutableCapabilities sauceOptions = new MutableCapabilities();
        sauceOptions.setCapability("accessKey", "2secret4u");

        capabilities.setCapability("sauce:options", sauceOptions);

        assertThat(capabilities.toString())
                .isEqualTo("{sauce:options: {accessKey: ********}}");
    }
}
