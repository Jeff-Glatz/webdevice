[![Build Status](https://img.shields.io/travis/ruffkat/webdevice/master?color=green)](https://travis-ci.com/ruffkat/webdevice)
[![Code Coverage](https://codecov.io/gh/ruffkat/webdevice/branch/master/graph/badge.svg)](https://codecov.io/gh/ruffkat/webdevice)
[![Maven Central](https://img.shields.io/maven-central/v/io.webdevice/webdevice-spring-boot.svg?color=green&label=maven%20central)](https://search.maven.org/search?q=g:io.webdevice)

# WebDevice.IO
WebDevice.IO is a lightweight Java-based framework for managing a collection of predefined WebDriver 
instances for use in browser automation test suites. The instances can be local, remote, and works well
with Sauce Labs for driving desktop and mobile web devices.

## Installation
```xml
<dependency>
  <groupId>io.webdevice</groupId>
  <artifactId>webdevice-spring-boot</artifactId>
  <version>0.0.13</version>
</dependency>
```
## Spring Integration
`WebDevice` works best with Spring. When used with Spring, the `WebDevice` runtime will manage the lifecycle of all
`WbeDriver` instances so that they will be automatically closed and quit. 

### Spring Test Framework
If using plain Spring Test Framework, use the `webdevice-spring-base` artifact:
```xml
<dependency>
  <groupId>io.webdevice</groupId>
  <artifactId>webdevice-spring-base</artifactId>
  <version>0.0.13</version>
</dependency>
```
To activate the `WebDevice` runtime, apply the `@EnableWebDevice` annotation:
```java
package com.someco.product.cucumber.steps;

import io.webdevice.device.WebDevice;
import io.webdevice.wiring.EnableWebDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@EnableWebDevice
@ContextConfiguration(classes = TestConfiguration.class)
public class TestCase {

    @Autowired
    private WebDevice browser;

    @Test
    public void shouldDoSomething() {
        browser.use("iPhone8");
        browser.home();
        browser.navigateTo(relativePath);
    }
}
```

### Spring Boot Test Framework
As long as the `webdevice-spring-boot` artifact is on the classpath, the runtime will be activated automatically:
```java
package com.someco.product.cucumber.steps;

import io.webdevice.device.WebDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = TestConfiguration.class)
public class TestSteps {

    @Autowired
    private WebDevice browser;

    @Test
    public void shouldDoSomething() {
        browser.use("iPhone8");
        browser.home();
        browser.navigateTo(relativePath);
    }
}
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

#### With Existing Configuration
In the most common use case, `WebDevice` will be integrated into a test automation suite with an existing Spring
configuration. In this case, apply the `@EnableWebDevice` annotation to activate the runtime:
```java
package com.someco.product.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.spring.CucumberContextConfiguration;
import io.webdevice.device.WebDevice;
import io.webdevice.wiring.EnableWebDevice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@EnableWebDevice
@CucumberContextConfiguration
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

#### Without Existing Configuration
If the project utilizing WebDevice does not have an existing spring configuration and simply wants access
to the `WebDevice` instance, then the `WebDeviceRuntime` can be referenced directly:
```java
package com.someco.product.cucumber.steps;

import io.cucumber.java.en.Given;
import io.cucumber.spring.CucumberContextConfiguration;
import io.webdevice.device.WebDevice;
import io.webdevice.wiring.WebDeviceRuntime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
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

#### Specifying iPhone Device via SauceLabs/Appium
```yaml
webdevice:
  devices:
    iPhone8:
      remote-address: https://ondemand.saucelabs.com:443/wd/hub
      aliases: iPhone 8
      pooled: false
      desired: iphone
      capabilities:
        username: ${saucelabs_username}
        accessKey: ${saucelabs_accessKey}
        extendedDebugging: true
        appiumVersion: "1.13.0"
        deviceName: iPhone 8
        deviceOrientation: portrait
        platformVersion: "12.2"
        platformName: iOS
        browserName: Safari
      confidential:
        - accessKey    
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
    public DeviceProvider<RemoteWebDriver> myDevice() {
        return remoteProvider("myDevice", this::customDriver);
    }

    private RemoteWebDriver customDriver() {
        // Build up the custom RemoteWebDriver
        return (RemoteWebDriver) builder()
                .addMetadata("key", "value")
                .build();
    }
}
```

## Big Thanks!

Cross-browser Testing Platform and Open Source ❤️ provided by [Sauce Labs](https://saucelabs.com)

[homepage]: https://saucelabs.com