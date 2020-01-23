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

public class WebDeviceListenerTest
        extends UnitTest {
    private WebDeviceListener listener;

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
        listener = new WebDeviceListener();
    }

    @Test
    public void shouldDoNothingWhenScopeIsNull() {
        given(mockTestContext.getApplicationContext())
                .willReturn(mockApplicationContext);

        listener.afterTestMethod(mockTestContext);

        verify(mockTestContext)
                .getApplicationContext();
        verifyNoMoreInteractions(mockConfigurableContext,
                mockApplicationContext,
                mockTestContext);
    }

    @Test
    public void shouldDoNothingWhenScopeWasNotDisposed() {
        WebDeviceScope scope = new WebDeviceScope();
        assertThat(scope.dispose())
                .isFalse();

        given(mockTestContext.getApplicationContext())
                .willReturn(mockConfigurableContext);
        given(mockConfigurableContext.getBeanFactory())
                .willReturn(mockBeanFactory);
        given(mockBeanFactory.getRegisteredScope("web-device"))
                .willReturn(scope);

        listener.afterTestMethod(mockTestContext);

        verify(mockTestContext)
                .getApplicationContext();
        verify(mockConfigurableContext)
                .getBeanFactory();
        verifyNoMoreInteractions(mockConfigurableContext,
                mockApplicationContext,
                mockTestContext);
    }

    @Test
    public void shouldSetReinjectDepedenciesAttributeWhenScopeIsDisposed() {
        WebDeviceScope scope = new WebDeviceScope();
        scope.get("prototype", () -> mock(WebDevice.class));

        given(mockTestContext.getApplicationContext())
                .willReturn(mockConfigurableContext);
        given(mockConfigurableContext.getBeanFactory())
                .willReturn(mockBeanFactory);
        given(mockBeanFactory.getRegisteredScope("web-device"))
                .willReturn(scope);

        listener.afterTestMethod(mockTestContext);

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
}
