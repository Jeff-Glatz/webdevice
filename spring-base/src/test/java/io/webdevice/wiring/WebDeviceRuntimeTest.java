package io.webdevice.wiring;

import io.webdevice.device.WebDevice;
import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class WebDeviceRuntimeTest
        extends UnitTest {
    @Mock
    private ConfigurableApplicationContext mockConfigurableContext;
    @Mock
    private ConfigurableListableBeanFactory mockBeanFactory;
    @Mock
    private WebDevice mockWebDevice;
    private ContextClosedEvent closedEvent;
    private WebDeviceRuntime runtime;

    @Before
    public void setUp() {
        closedEvent = new ContextClosedEvent(mockConfigurableContext);
        runtime = new WebDeviceRuntime();
    }

    @Test
    public void shouldDoNothingWhenScopeIsNullWhenContextClosed() {
        given(mockConfigurableContext.getBeanFactory())
                .willReturn(mockBeanFactory);
        given(mockBeanFactory.getRegisteredScope("webdevice"))
                .willReturn(null);

        ApplicationListener<ContextClosedEvent> disposer = runtime.webDeviceRuntimeDisposer();
        disposer.onApplicationEvent(closedEvent);

        verify(mockConfigurableContext)
                .getBeanFactory();
        verify(mockBeanFactory)
                .getRegisteredScope("webdevice");
        verifyNoMoreInteractions(mockConfigurableContext,
                mockBeanFactory, mockWebDevice);
    }

    @Test
    public void shouldDoNothingWhenScopeIsPresentAndEmptyWhenContextClosed() {
        WebDeviceScope scope = new WebDeviceScope();
        assertThat(scope.isEmpty())
                .isTrue();

        given(mockConfigurableContext.getBeanFactory())
                .willReturn(mockBeanFactory);
        given(mockBeanFactory.getRegisteredScope("webdevice"))
                .willReturn(scope);

        ApplicationListener<ContextClosedEvent> disposer = runtime.webDeviceRuntimeDisposer();
        disposer.onApplicationEvent(closedEvent);

        verify(mockConfigurableContext)
                .getBeanFactory();
        verify(mockBeanFactory)
                .getRegisteredScope("webdevice");
        verifyNoMoreInteractions(mockConfigurableContext,
                mockBeanFactory, mockWebDevice);
    }

    @Test
    public void shouldDisposeWhenScopeIsPresentAndNotEmptyWhenContextClosed() {
        WebDeviceScope scope = new WebDeviceScope();
        scope.get("prototype", () -> mockWebDevice);
        scope.registerDestructionCallback("prototype", mockWebDevice::release);

        assertThat(scope.isEmpty())
                .isFalse();

        given(mockConfigurableContext.getBeanFactory())
                .willReturn(mockBeanFactory);
        given(mockBeanFactory.getRegisteredScope("webdevice"))
                .willReturn(scope);

        ApplicationListener<ContextClosedEvent> disposer = runtime.webDeviceRuntimeDisposer();
        disposer.onApplicationEvent(closedEvent);

        assertThat(scope.isEmpty())
                .isTrue();

        verify(mockConfigurableContext)
                .getBeanFactory();
        verify(mockBeanFactory)
                .getRegisteredScope("webdevice");
        verify(mockWebDevice)
                .release();
        verifyNoMoreInteractions(mockConfigurableContext,
                mockBeanFactory, mockWebDevice);
    }
}
