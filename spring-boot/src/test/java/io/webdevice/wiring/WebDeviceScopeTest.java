package io.webdevice.wiring;

import io.webdevice.device.WebDevice;
import io.webdevice.test.UnitTest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

import static io.webdevice.wiring.WebDeviceScope.registerScope;
import static io.webdevice.wiring.WebDeviceScope.scope;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class WebDeviceScopeTest
        extends UnitTest {
    private WebDeviceScope scope;

    @Mock
    private ApplicationContext mockApplicationContext;

    @Mock
    private ConfigurableApplicationContext mockConfigurableContext;

    @Mock
    private ConfigurableListableBeanFactory mockBeanFactory;

    @Mock
    private WebDevice mockWebDevice;

    @Before
    public void setUp() {
        scope = new WebDeviceScope();
    }

    @Test
    public void shouldRegisterAndReturnScope() {
        scope = registerScope(mockBeanFactory);

        assertThat(scope)
                .isNotNull();
        verify(mockBeanFactory)
                .registerScope("webdevice", scope);
    }

    @Test
    public void shouldCreatePrototypes() {
        ObjectFactory<Object> factory = () -> new WebDevice(null);

        assertThat(scope.get("webdevice.WebDevice", factory))
                .isNotSameAs(scope.get("webdevice.WebDevice", factory));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldRemovePrototypes() {
        WebDevice device1 = new WebDevice(null);
        WebDevice device2 = new WebDevice(null);

        scope.get("webdevice.WebDevice", () -> device1);
        scope.get("webdevice.WebDevice", () -> device2);

        assertThat(scope.isEmpty())
                .isFalse();

        List<Object> prototypes = (List<Object>) scope.remove("webdevice.WebDevice");

        assertThat(prototypes).contains(device1, device2);

        assertThat(scope.isEmpty())
                .isTrue();
    }

    @Test
    public void shouldReturnFalseFromDisposeWhenEmpty() {
        assertThat(scope.dispose())
                .isFalse();
    }

    @Test
    public void shouldReturnFalseFromDisposeWhenNotEmptyButNoWebDevices() {
        scope.get("prototype", () -> "Hai");

        assertThat(scope.dispose())
                .isFalse();
    }

    @Test
    public void shouldReturnTrueFromDisposeWhenNotEmptyWithWebDevices() {
        scope.get("prototype", () -> "Hai");
        scope.get("prototype", () -> mockWebDevice);

        assertThat(scope.dispose())
                .isTrue();
    }

    @Test
    public void shouldClearInstancesOnDispose() {
        scope.get("prototype", () -> "Hai");

        assertThat(scope.isEmpty())
                .isFalse();

        scope.dispose();

        assertThat(scope.isEmpty())
                .isTrue();
    }

    @Test
    public void shouldReleaseWebDeviceOnDispose() {
        scope.get("prototype", () -> mockWebDevice);

        scope.dispose();

        verify(mockWebDevice)
                .release();
    }

    @Test
    public void shouldNotExtractScopeFromApplicationContextWhenNotConfigurable() {
        scope = scope(mockApplicationContext);

        assertThat(scope)
                .isNull();
        verifyNoMoreInteractions(mockConfigurableContext,
                mockApplicationContext,
                mockBeanFactory);
    }

    @Test
    public void shouldExtractScopeFromApplicationContextWhenConfigurable() {
        given(mockConfigurableContext.getBeanFactory())
                .willReturn(mockBeanFactory);
        given(mockBeanFactory.getRegisteredScope("webdevice"))
                .willReturn(new WebDeviceScope());

        scope = scope(mockConfigurableContext);

        assertThat(scope)
                .isNotNull();
        verify(mockConfigurableContext)
                .getBeanFactory();
        verifyNoMoreInteractions(mockConfigurableContext,
                mockApplicationContext);
    }
}
