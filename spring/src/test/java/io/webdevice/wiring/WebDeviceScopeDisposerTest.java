package io.webdevice.wiring;

import io.webdevice.device.WebDevice;
import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.TestContext;

import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.springframework.test.context.support.DependencyInjectionTestExecutionListener.REINJECT_DEPENDENCIES_ATTRIBUTE;

public class WebDeviceScopeDisposerTest
        extends UnitTest {
    private WebDeviceScopeDisposer disposer;

    @Mock
    private TestContext mockTestContext;

    @Mock
    private ApplicationContext mockApplicationContext;

    @Mock
    private ConfigurableApplicationContext mockConfigurableContext;

    @Mock
    private ConfigurableListableBeanFactory mockBeanFactory;

    @Before
    public void setUp() {
        disposer = new WebDeviceScopeDisposer();
    }

    @Test
    public void shouldDoNothingWhenScopeIsNullAfterTestMethod() {
        given(mockTestContext.getApplicationContext())
                .willReturn(mockApplicationContext);

        disposer.afterTestMethod(mockTestContext);

        verify(mockTestContext)
                .getApplicationContext();
        verifyNoMoreInteractions(mockConfigurableContext,
                mockApplicationContext,
                mockTestContext);
    }

    @Test
    public void shouldDoNothingWhenScopeWasNotDisposedAfterTestMethod() {
        WebDeviceScope scope = new WebDeviceScope();
        assertThat(scope.dispose())
                .isFalse();

        given(mockTestContext.getApplicationContext())
                .willReturn(mockConfigurableContext);
        given(mockConfigurableContext.getBeanFactory())
                .willReturn(mockBeanFactory);
        given(mockBeanFactory.getRegisteredScope("webdevice"))
                .willReturn(scope);

        disposer.afterTestMethod(mockTestContext);

        verify(mockTestContext)
                .getApplicationContext();
        verify(mockConfigurableContext)
                .getBeanFactory();
        verifyNoMoreInteractions(mockConfigurableContext,
                mockApplicationContext,
                mockTestContext);
    }

    @Test
    public void shouldSetReinjectDependenciesAttributeWhenScopeIsDisposedAfterTestMethod() {
        WebDeviceScope scope = new WebDeviceScope();
        scope.get("prototype", () -> mock(WebDevice.class));

        assertThat(scope.isEmpty())
                .isFalse();

        given(mockTestContext.getApplicationContext())
                .willReturn(mockConfigurableContext);
        given(mockConfigurableContext.getBeanFactory())
                .willReturn(mockBeanFactory);
        given(mockBeanFactory.getRegisteredScope("webdevice"))
                .willReturn(scope);

        disposer.afterTestMethod(mockTestContext);

        assertThat(scope.isEmpty())
                .isTrue();

        verify(mockTestContext)
                .getApplicationContext();
        verify(mockConfigurableContext)
                .getBeanFactory();
        verify(mockTestContext)
                .setAttribute(REINJECT_DEPENDENCIES_ATTRIBUTE, TRUE);
        verifyNoMoreInteractions(mockConfigurableContext,
                mockApplicationContext,
                mockTestContext);
    }

    @Test
    public void shouldDoNothingWhenScopeIsNullAfterTestClass() {
        given(mockTestContext.getApplicationContext())
                .willReturn(mockApplicationContext);

        disposer.afterTestClass(mockTestContext);

        verify(mockTestContext)
                .getApplicationContext();
        verifyNoMoreInteractions(mockConfigurableContext,
                mockApplicationContext,
                mockTestContext);
    }

    @Test
    public void shouldDoNothingWhenScopeIsPresentAndEmptyAfterTestClass() {
        WebDeviceScope scope = new WebDeviceScope();
        assertThat(scope.isEmpty())
                .isTrue();

        given(mockTestContext.getApplicationContext())
                .willReturn(mockConfigurableContext);
        given(mockConfigurableContext.getBeanFactory())
                .willReturn(mockBeanFactory);
        given(mockBeanFactory.getRegisteredScope("webdevice"))
                .willReturn(scope);

        disposer.afterTestClass(mockTestContext);

        verify(mockTestContext)
                .getApplicationContext();
        verify(mockConfigurableContext)
                .getBeanFactory();
        verifyNoMoreInteractions(mockConfigurableContext,
                mockApplicationContext,
                mockTestContext);
    }

    @Test
    public void shouldDisposeWhenScopeIsPresentAndNotEmptyAfterTestMethod() {
        WebDeviceScope scope = new WebDeviceScope();
        scope.get("prototype", () -> mock(WebDevice.class));

        assertThat(scope.isEmpty())
                .isFalse();

        given(mockTestContext.getApplicationContext())
                .willReturn(mockConfigurableContext);
        given(mockConfigurableContext.getBeanFactory())
                .willReturn(mockBeanFactory);
        given(mockBeanFactory.getRegisteredScope("webdevice"))
                .willReturn(scope);

        disposer.afterTestClass(mockTestContext);

        assertThat(scope.isEmpty())
                .isTrue();

        verify(mockTestContext)
                .getApplicationContext();
        verify(mockConfigurableContext)
                .getBeanFactory();
        verifyNoMoreInteractions(mockConfigurableContext,
                mockApplicationContext,
                mockTestContext);
    }
}
