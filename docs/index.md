[![Build Status](https://img.shields.io/travis/ruffkat/webdevice/master?color=green)](https://travis-ci.com/ruffkat/webdevice)
[![Code Coverage](https://codecov.io/gh/ruffkat/webdevice/branch/master/graph/badge.svg)](https://codecov.io/gh/ruffkat/webdevice)
[![Maven Central](https://img.shields.io/maven-central/v/io.webdevice/webdevice-spring.svg?color=green&label=maven%20central)](https://search.maven.org/search?q=g:io.webdevice)

# WebDevice.IO
WebDevice.IO is a lightweight Java-based framework, initially for use within `Cucumber` test suites 
based on `cucumber-spring`, for managing a collection of `WebDriver` devices that can be specified and
activated at `Scenario` runtime.

## Installation
```xml
<dependency>
  <groupId>io.webdevice</groupId>
  <artifactId>webdevice-spring</artifactId>
  <version>0.0.8</version>
</dependency>
```

## Cucumber Integration
### Cucumber + Spring + JUnit
#### Test Suite Runner
```java
package com.someco.product.cucumber.runner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        glue = {"com.someco.product.cucumber.steps"},
        features = {"src/test/resources/features"},
        plugin = {"pretty"})
public class TestSuite {
}
```
#### Primary Step Definition
When using `cucumber-spring`, only one step definition is allowed to carry the context configuring
Spring annotation.

#### Without Existing Configuration
```java
package com.someco.product.cucumber.steps;

import io.cucumber.java.en.Given;
import io.webdevice.device.WebDevice;
import io.webdevice.wiring.WebDeviceRuntime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = WebDeviceRuntime.class)
public class TestSteps {

    @Autowired
    private WebDevice browser;

    @Given("a {string} browser")
    public void useBrowser(String name) {
        browser.use(name);
    }

    @Given("a browser")
    public void useBrowser() {
        browser.useDefault();
    }

    @Given("I navigate home")
    public void navigateHome() {
        browser.home();
    }

    @Given("I navigate to {string}")
    public void navigateTo(String relativePath) {
        browser.navigateTo(relativePath);
    }
}
```

#### With Existing Configuration
```java
package com.someco.product.cucumber.steps;

import io.cucumber.java.en.Given;
import io.webdevice.device.WebDevice;
import io.webdevice.wiring.EnableWebDevice;
import io.webdevice.wiring.WebDeviceRuntime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@EnableWebDevice
@SpringBootTest(classes = TestConfiguration.class)
public class TestSteps {

    @Autowired
    private WebDevice browser;

    @Given("a {string} browser")
    public void useBrowser(String name) {
        browser.use(name);
    }

    @Given("a browser")
    public void useBrowser() {
        browser.useDefault();
    }

    @Given("I navigate home")
    public void navigateHome() {
        browser.home();
    }

    @Given("I navigate to {string}")
    public void navigateTo(String relativePath) {
        browser.navigateTo(relativePath);
    }
}
```

## Declarative Device Definition
WebDevice.IO makes it simple to define various browser profiles for use in your automated tests. It supports 
direct browser control as well as remote browser control. WebDevice definitions can be added to your
`application.yaml` file.

### Direct WebDriver Definition
WebDriver.IO leverages the [WebDriverManager](https://github.com/bonigarcia/webdrivermanager) library to set up the environment for direct browser control. 
To indicate that a device definition is a direct `WebDriver`, the `driver` property must be defined:

```yaml
webdevice:
    devices:
        DirectDevice:
            driver: org.openqa.selenium.firefox.FirefoxDriver
            aliases: Firefox, Local Firefox
```

### Remote WebDriver Definition
`RemoteWebDriver` instances can be defined in various ways, primarily variation occurs when specifying
the capabilities of the driver. To indicate that a device definition is a `RemoteWebDriver`, the
`remote-address` property must be defined:

```yaml
webdevice:
    devices:
        RemoteDevice:
            remote-address: https://ondemand.saucelabs.com:443/wd/hub
```

### Capabilities Specification
For both direct and remote drivers, WebDevice.IO supports various means of specifying the `Capabilities`
of the device.

#### Using Options
Browser specific `Options` can be used as the originating capabilities source, where capabilities 
are merged into the `Options` instance:

```yaml
webdevice:
    devices:
        FirefoxLatestMojave:
            remote-address: https://ondemand.saucelabs.com:443/wd/hub
            aliases: Firefox, Firefox Mojave
            pooled: false
            options: org.openqa.selenium.firefox.FirefoxOptions
            capabilities:
                username: ${saucelabs_username}
                accessKey: ${saucelabs_accessKey}
                extendedDebugging: true
                platform: macOS 10.14
                version: latest
```

#### Using DesiredCapabilities
The static factory methods on `DesiredCapabilities` (in this example `DesiredCapabilities.safari()`) 
can be used as the originating capabilities source, where capabilities are merged into the 
`DesiredCapabilities` instance:

```yaml
webdevice:
    devices:
        SafariLatestMojave:
            remote-address: https://ondemand.saucelabs.com:443/wd/hub
            pooled: false
            desired: safari
            capabilities:
                username: ${saucelabs_username}
                accessKey: ${saucelabs_accessKey}
                extendedDebugging: true
                platform: macOS 10.14
                version: latest
```

#### Using Capabilities
It is also possible to construct a generic `Capabilities` instance directly:

```yaml
webdevice:
    devices:
        Chrome59Windows10:
            remote-address: https://ondemand.saucelabs.com:443/wd/hub
            pooled: false
            aliases: Chrome v59 Windows 10
            capabilities:
                username: ${saucelabs_username}
                accessKey: ${saucelabs_accessKey}
                browserName: Chrome
                platform: Windows 10
                version: "59.0"
                extendedDebugging: true
```

#### Using Capabilities Bean
Devices can take their capabilities from a `Capabilities` bean defined in the spring context:

```yaml
webdevice:
    devices:
        RemoteDevice:
            remote-address: https://ondemand.saucelabs.com:443/wd/hub
            pooled: false
            capabilities-ref: remoteDeviceCapabilities
```

#### Specifying Extra Options
Here is an example definition adding sauce options:

```yaml
webdevice:
    devices:
        ChromeLatestMojave:
            remote-address: https://ondemand.saucelabs.com:443/wd/hub
            pooled: false
            options: org.openqa.selenium.chrome.ChromeOptions
            capabilities:
                w3c: true
                platformName: macOS 10.14
                browserVersion: latest
            extra-capability: sauce:options
            extra-options:
                username: ${saucelabs_username}
                accessKey: ${saucelabs_accessKey}
                extendedDebugging: true
```

## Programmatic Device Definition
If the declarative abilities of WebDevice.IO are not sufficient, then simply provide your own `DeviceProvider`
implementation(s):

```java
import io.webdevice.device.DeviceProvider;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static io.webdevice.device.Devices.remoteProvider;
import static org.openqa.selenium.remote.RemoteWebDriver.builder;

@Configuration
public class CustomProviderWiring {

    @Bean
    public DeviceProvider<RemoteWebDriver> customRemoteProvider() {
        return remoteProvider("customRemoteProvider",
                this::customRemoteDriver);
    }

    private RemoteWebDriver customRemoteDriver() {
        return (RemoteWebDriver) builder()
                .addMetadata("key", "value")
                .build();
    }
}
```

## Big Thanks!

Cross-browser Testing Platform and Open Source ❤️ provided by [Sauce Labs](https://saucelabs.com)

[homepage]: https://saucelabs.com