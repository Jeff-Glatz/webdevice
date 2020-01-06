package io.webdevice.support;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.MutableCapabilities;

import java.util.LinkedHashSet;
import java.util.Set;

import static io.webdevice.support.ProtectedCapabilities.mask;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

public class ProtectedCapabilitiesTest {
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

    @Test
    public void maskShouldProtectConfidentialCapabilities() {
        MutableCapabilities capabilities = new MutableCapabilities();
        capabilities.setCapability("accessKey", "2secret4u");

        assertThat(mask(capabilities, singleton("accessKey")))
                .isEqualTo("{accessKey: ********}");
    }

    @Test
    public void shouldMimicCapabilitiesToString() {
        capabilities.setCapability("array", new String[]{"one", null});
        capabilities.setCapability("list", singletonList("one"));
        capabilities.setCapability("null", (String)null);
        capabilities.setCapability("truncated", "1234567890123456789012345678901");

        assertThat(capabilities.toString())
                .isEqualTo("{array: [one, null], list: [one], truncated: 123456789012345678901234567...}");
    }
}
