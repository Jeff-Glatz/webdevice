package io.webdevice.settings;

import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.net.URL;

import static io.bestquality.util.MapBuilder.mapOf;
import static org.assertj.core.api.Assertions.assertThat;

public class DefaultSettingsBinderTest
        extends EnvironmentBasedTest {
    private DefaultSettingsBinder binder;

    @Before
    public void setUp() {
        binder = new DefaultSettingsBinder();
    }

    @Test
    public void shouldBindSettings()
            throws Exception {
        environmentWith(mapOf(String.class, Object.class)
                .with("webdevice.base-url", "https://webdevice.io")
                .with("webdevice.default-device", "Firefox Local")
                .with("webdevice.eager", "true")
                .with("webdevice.strict", "false")
                .with("webdevice.scope", "prototype")
                // Firefox
                .with("webdevice.devices[Firefox].driver", FirefoxDriver.class.getName())
                .with("webdevice.devices[Firefox].aliases", "Firefox Local")
                .with("webdevice.devices[Firefox].pooled", "true")
                .with("webdevice.devices[Firefox].capabilities[version]", "59")
                // iPhone 8
                .with("webdevice.devices[iPhone8].remote-address", "https://ondemand.saucelabs.com:443/wd/hub")
                .with("webdevice.devices[iPhone8].aliases[0]", "iPhone")
                .with("webdevice.devices[iPhone8].aliases[1]", "iPhone 8")
                .with("webdevice.devices[iPhone8].pooled", "false")
                .with("webdevice.devices[iPhone8].capabilities[username]", "${saucelabs_username}")
                .with("webdevice.devices[iPhone8].capabilities[accessKey]", "${saucelabs_accessKey}")
                .with("webdevice.devices[iPhone8].capabilities[extendedDebugging]", "true")
                .with("webdevice.devices[iPhone8].capabilities[appiumVersion]", "1.13.0")
                .with("webdevice.devices[iPhone8].capabilities[deviceName]", "iPhone 8")
                .with("webdevice.devices[iPhone8].capabilities[deviceOrientation]", "portrait")
                .with("webdevice.devices[iPhone8].capabilities[platformVersion]", "12.2")
                .with("webdevice.devices[iPhone8].capabilities[platformName]", "iOS")
                .with("webdevice.devices[iPhone8].capabilities[browserName]", "Safari")
                .with("webdevice.devices[iPhone8].confidential[0]", "username")
                .with("webdevice.devices[iPhone8].confidential[1]", "accessKey")
                // Chrome
                .with("webdevice.devices.Chrome.driver", FirefoxDriver.class.getName())
                .with("webdevice.devices.Chrome.aliases", "Chrome Local, Chrome Default")
                .with("webdevice.devices.Chrome.capabilities.version", "59")
                .build());

        Settings actual = binder.from(environment);
        assertThat(actual)
                .isEqualTo(new Settings()
                        .withBaseUrl(new URL("https://webdevice.io"))
                        .withDefaultDevice("Firefox Local")
                        .withEager(true)
                        .withStrict(false)
                        .withScope("prototype")
                        .withDevice(new DeviceDefinition()
                                .withName("Firefox")
                                .withDriver(FirefoxDriver.class)
                                .withAlias("Firefox Local")
                                .withPooled(true)
                                .withCapability("version", "59"))
                        .withDevice(new DeviceDefinition()
                                .withName("iPhone8")
                                .withRemoteAddress(new URL("https://ondemand.saucelabs.com:443/wd/hub"))
                                .withAlias("iPhone")
                                .withAlias("iPhone 8")
                                .withPooled(false)
                                .withCapability("username", "saucy")
                                .withCapability("accessKey", "2secret4u")
                                .withCapability("extendedDebugging", "true")
                                .withCapability("appiumVersion", "1.13.0")
                                .withCapability("deviceName", "iPhone 8")
                                .withCapability("deviceOrientation", "portrait")
                                .withCapability("platformVersion", "12.2")
                                .withCapability("platformName", "iOS")
                                .withCapability("browserName", "Safari")
                                .withConfidential("username")
                                .withConfidential("accessKey"))
                        .withDevice(new DeviceDefinition()
                                .withName("Chrome")
                                .withDriver(FirefoxDriver.class)
                                .withAlias("Chrome Local")
                                .withAlias("Chrome Default")
                                .withPooled(false)
                                .withCapability("version", "59")));
    }
}
