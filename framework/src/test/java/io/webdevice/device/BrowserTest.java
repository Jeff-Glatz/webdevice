package io.webdevice.device;

import io.webdevice.support.RelativeNavigation;
import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriver.Navigation;
import org.openqa.selenium.WebDriver.Options;
import org.openqa.selenium.WebDriver.TargetLocator;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Interactive;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebElement;

import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.webdevice.device.Devices.directDevice;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.OutputType.BASE64;
import static org.openqa.selenium.remote.DesiredCapabilities.iphone;

public class BrowserTest
        extends UnitTest {
    @Mock
    private DeviceRegistry mockDeviceRegistry;

    // First mock device
    @Mock
    private TargetLocator mockTargetLocator;
    @Mock
    private Navigation mockNavigation;
    @Mock
    private Options mockOptions;
    @Mock(extraInterfaces = {
            JavascriptExecutor.class, HasCapabilities.class,
            Interactive.class, TakesScreenshot.class
    })
    private WebDriver mockWebDriver;
    private Device<WebDriver> device;

    // Second mock device
    @Mock(extraInterfaces = {
            JavascriptExecutor.class, HasCapabilities.class,
            Interactive.class, TakesScreenshot.class
    })
    private WebDriver mockWebDriver2;
    private Device<WebDriver> device2;

    private Browser browser;

    @Mock
    private Consumer<WebDriver> mockConsumer;
    // This function can return anything, for the test this will behave like perform()
    @Mock
    private Function<WebDriver, WebDevice> mockFunction;


    @Before
    public void setUp()
            throws Exception {
        given(mockWebDriver.navigate())
                .willReturn(mockNavigation);
        given(mockWebDriver.switchTo())
                .willReturn(mockTargetLocator);
        given(mockWebDriver.manage())
                .willReturn(mockOptions);

        device = directDevice("iphone", mockWebDriver);

        device2 = directDevice("ipad", mockWebDriver2);

        browser = new Browser(mockDeviceRegistry)
                .withBaseUrl(new URL("http://localhost"));
    }

    @Test
    public void fluentBuildersShouldPopulateProperties()
            throws
            Exception {
        browser.withBaseUrl(new URL("http://remotehost"))
                .withDefaultDevice("iphone")
                .withEager(true)
                .withStrict(true);

        assertThat(browser.getBaseUrl())
                .isEqualTo(new URL("http://remotehost"));
        assertThat(browser.getDefaultDevice())
                .isEqualTo("iphone");
        assertThat(browser.isEager())
                .isEqualTo(true);
        assertThat(browser.isStrict())
                .isEqualTo(true);
    }

    @Test
    public void shouldCanonicalizeUrls()
            throws Exception {
        assertThat(browser.canonicalize("http://remotehost"))
                .isEqualTo("http://remotehost");

        assertThat(browser.canonicalize("foo"))
                .isEqualTo("http://localhost/foo");
        assertThat(browser.canonicalize("/foo"))
                .isEqualTo("http://localhost/foo");

        browser.withBaseUrl(new URL("http://localhost/"));

        assertThat(browser.canonicalize("foo"))
                .isEqualTo("http://localhost/foo");
        assertThat(browser.canonicalize("/foo"))
                .isEqualTo("http://localhost/foo");
    }

    @Test
    public void initializeShouldAcquireDefaultDevice() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        assertThat(browser.device())
                .isSameAs(device);
    }

    @Test
    public void initializeShouldNotAcquireDefaultDevice() {
        initializeWith("iphone", false, true);

        assertThat(browser.acquired())
                .isFalse();
    }

    @Test
    public void useShouldRaiseExceptionWhenStrictAndDeviceAlreadyAcquired() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        assertThat(browser.acquired())
                .isTrue();

        try {
            browser.use("iphone");
            fail("Expected an exception");
        } catch (IllegalStateException e) {
            assertThat(e).
                    hasMessage("Browser has already been acquired for the current scenario");
        }
    }

    @Test
    public void useShouldReleaseDeviceWhenNotStrictAndDeviceAlreadyAcquired() {
        providing("iphone", device)
                .providing("ipad", device2)
                .initializeWith("iphone", true, false);

        assertThat(browser.device())
                .isSameAs(device);

        browser.use("ipad");

        assertThat(browser.device())
                .isSameAs(device2);

        verify(mockDeviceRegistry)
                .release(device);
    }

    @Test
    public void useDefaultShouldAcquireDefaultDevice() {
        providing("iphone", device)
                .initializeWith("iphone", false, true);

        assertThat(browser.acquired())
                .isFalse();

        browser.useDefault();

        assertThat(browser.device())
                .isSameAs(device);
    }

    @Test
    public void homeShouldNavigateToBaseUrl()
            throws Exception {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        browser.home();

        verify(mockNavigation)
                .to(new URL("http://localhost"));
    }

    @Test
    public void navigateToShouldCanonicalizeRelativeUrl() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        browser.navigateTo("/foo");

        verify(mockNavigation)
                .to("http://localhost/foo");
    }

    @Test
    public void navigateToShouldNotCanonicalizeAbsoluteUrl() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        browser.navigateTo("http://remotehost/foo");

        verify(mockNavigation)
                .to("http://remotehost/foo");
    }

    @Test
    public void performShouldExecuteWithDriver() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        browser.perform(mockConsumer);

        verify(mockConsumer)
                .accept(mockWebDriver);
    }

    @Test
    public void invokeShouldExecuteWithDriver() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        given(mockFunction.apply(mockWebDriver))
                .willReturn(browser);

        WebDevice fluent = browser.invoke(mockFunction);

        assertThat(fluent)
                .isSameAs(browser);
        verify(mockFunction)
                .apply(mockWebDriver);
    }

    // HasCapabilities delegate

    @Test
    public void getCapabilitiesShouldDelegate() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        Capabilities expected = iphone();
        given(((HasCapabilities) mockWebDriver).getCapabilities())
                .willReturn(expected);

        assertThat(browser.getCapabilities())
                .isSameAs(expected);
    }

    // JavascriptExecutor delegates

    @Test
    public void executeScriptShouldDelegate() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        Object expected = new Object();

        given(((JavascriptExecutor) mockWebDriver).executeScript("script", "arg"))
                .willReturn(expected);

        assertThat(browser.executeScript("script", "arg"))
                .isSameAs(expected);
    }

    @Test
    public void executeAsyncScriptShouldDelegate() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        Object expected = new Object();

        given(((JavascriptExecutor) mockWebDriver).executeAsyncScript("script", "arg"))
                .willReturn(expected);

        assertThat(browser.executeAsyncScript("script", "arg"))
                .isSameAs(expected);
    }

    // TakesScreenshot delegate

    @Test
    public void getScreenshotAsShouldDelegate() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        String expected = "encoded";

        given(((TakesScreenshot) mockWebDriver).getScreenshotAs(BASE64))
                .willReturn(expected);

        assertThat(browser.getScreenshotAs(BASE64))
                .isSameAs(expected);
    }

    // WebDriver delegates

    @Test
    public void getShouldDelegateAndCanonicalizeRelativeUrl() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        browser.get("/foo");

        verify(mockWebDriver)
                .get("http://localhost/foo");
    }

    @Test
    public void getShouldDelegateAndNotCanonicalizeAbsoluteUrl() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        browser.get("http://remotehost/foo");

        verify(mockWebDriver)
                .get("http://remotehost/foo");
    }

    @Test
    public void getCurrentUrlShouldDelegate() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        String expected = "http://localhost/foo";

        given(mockWebDriver.getCurrentUrl())
                .willReturn(expected);

        assertThat(browser.getCurrentUrl())
                .isSameAs(expected);
    }

    @Test
    public void getTitleShouldDelegate() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        String expected = "Foo";

        given(mockWebDriver.getTitle())
                .willReturn(expected);

        assertThat(browser.getTitle())
                .isSameAs(expected);
    }

    @Test
    public void findElementsShouldDelegate() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        By by = cssSelector("#id");
        List<WebElement> expected = emptyList();

        given(mockWebDriver.findElements(by))
                .willReturn(expected);

        assertThat(browser.findElements(by))
                .isSameAs(expected);
    }

    @Test
    public void findElementShouldDelegate() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        By by = cssSelector("#id");
        WebElement expected = new RemoteWebElement();

        given(mockWebDriver.findElement(by))
                .willReturn(expected);

        assertThat(browser.findElement(by))
                .isSameAs(expected);
    }

    @Test
    public void getPageSourceShouldDelegate() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        String expected = "<html/>";

        given(mockWebDriver.getPageSource())
                .willReturn(expected);

        assertThat(browser.getPageSource())
                .isSameAs(expected);
    }

    @Test
    public void closeShouldDelegateWhenThereAreMultipleWindowsOpen() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        given(mockWebDriver.getWindowHandles())
                .willReturn(new LinkedHashSet<>(asList("handle-1", "handle-2")));

        browser.close();

        verify(mockWebDriver)
                .close();
    }

    @Test
    public void closeShouldNotDelegateWhenThereIsOneWindowOpen() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        given(mockWebDriver.getWindowHandles())
                .willReturn(singleton("handle"));

        browser.close();

        verify(mockWebDriver, never())
                .close();
    }

    @Test
    public void quitShouldNotDelegate() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        browser.quit();

        verify(mockWebDriver, never())
                .quit();
    }

    @Test
    public void getWindowHandlesShouldDelegate() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        Set<String> expected = emptySet();

        given(mockWebDriver.getWindowHandles())
                .willReturn(expected);

        assertThat(browser.getWindowHandles())
                .isSameAs(expected);
    }

    @Test
    public void getWindowHandleShouldDelegate() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        String expected = "window-handle";

        given(mockWebDriver.getWindowHandle())
                .willReturn(expected);

        assertThat(browser.getWindowHandle())
                .isSameAs(expected);
    }

    @Test
    public void switchToShouldDelegate() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        assertThat(browser.switchTo())
                .isSameAs(mockTargetLocator);
    }

    @Test
    public void navigateShouldDelegateAndWrapResultWithRelativeNavigation() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        Navigation navigate = browser.navigate();
        navigate.to("/foo");

        assertThat(navigate)
                .isInstanceOf(RelativeNavigation.class);

        verify(mockWebDriver)
                .navigate();
        verify(mockNavigation)
                .to("http://localhost/foo");
    }

    @Test
    public void manageShouldDelegate() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        assertThat(browser.manage())
                .isSameAs(mockOptions);
    }

    // Interactive delegates

    @Test
    public void performActionsShouldDelegate() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        Collection<Sequence> actions = emptySet();

        browser.perform(actions);

        verify((Interactive) mockWebDriver)
                .perform(actions);
    }

    @Test
    public void resetInputStateShouldDelegate() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        browser.resetInputState();

        verify((Interactive) mockWebDriver)
                .resetInputState();
    }

    @Test
    public void releaseShouldNotFailWhenDeviceHasNotBeenAcquired() {
        assertThat(browser.acquired())
                .isFalse();

        browser.release();

        assertThat(browser.acquired())
                .isFalse();
    }

    @Test
    public void releaseShouldReleaseDeviceAndClearReferenceWhenOneHasBeenAcquired() {
        providing("iphone", device)
                .initializeWith("iphone", true, true);

        assertThat(browser.acquired())
                .isTrue();

        browser.release();

        assertThat(browser.acquired())
                .isFalse();

        verify(mockDeviceRegistry)
                .release(device);
    }

    @Test
    public void releaseShouldClearDeviceReferenceWhenExceptionIsRaisedReleasingToRegistry() {
        providing("iphone", device)
                .initializeWith("iphone", true, false);

        assertThat(browser.acquired())
                .isTrue();

        willThrow(new DeviceNotProvidedException("iphone"))
                .given(mockDeviceRegistry)
                .release(this.device);

        try {
            browser.release();
            fail("Expected an exception");
        } catch (DeviceNotProvidedException e) {
        }

        assertThat(browser.acquired())
                .isFalse();

        verify(mockDeviceRegistry)
                .release(this.device);
    }

    private BrowserTest providing(String name, Device<WebDriver> device) {
        given(mockDeviceRegistry.provide(name))
                .willReturn(device);
        return this;
    }

    private BrowserTest initializeWith(String defaultDevice, boolean eager, boolean strict) {
        browser.withDefaultDevice(defaultDevice)
                .withEager(eager)
                .withStrict(strict)
                .initialize();
        return this;
    }
}
