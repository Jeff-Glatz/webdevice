package io.webdevice.wiring;

import io.webdevice.device.Browser;
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

import static io.webdevice.wiring.WebDeviceScope.namespace;
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
    public void shouldReturnNullConversationId() {
        assertThat(scope.getConversationId())
                .isNull();
    }

    @Test
    public void shouldNotResolveContextualObject() {
        assertThat(scope.resolveContextualObject(null))
                .isNull();
        assertThat(scope.resolveContextualObject("webdevice"))
                .isNull();
    }

    @Test
    public void shouldRegisterDestructionCallback() {
        assertThat(scope.destructionCallbackRegistered("name"))
                .isFalse();

        scope.registerDestructionCallback("name", () -> {
        });

        assertThat(scope.destructionCallbackRegistered("name"))
                .isTrue();
    }

    @Test
    public void shouldSafelyDestroyWhenCallbackNotRegistered() {
        assertThat(scope.destructionCallbackRegistered("name"))
                .isFalse();

        assertThat(scope.safelyDestroy("name"))
                .isFalse();

        assertThat(scope.destructionCallbackRegistered("name"))
                .isFalse();
    }

    @Test
    public void shouldConsumeExceptionRaisedByCallback() {
        scope.registerDestructionCallback("name", () -> {
            throw new RuntimeException("boom");
        });

        assertThat(scope.destructionCallbackRegistered("name"))
                .isTrue();
        assertThat(scope.safelyDestroy("name"))
                .isFalse();
        assertThat(scope.destructionCallbackRegistered("name"))
                .isFalse();
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
        ObjectFactory<Object> factory = () -> new Browser(null);

        assertThat(scope.get("webdevice.WebDevice", factory))
                .isNotSameAs(scope.get("webdevice.WebDevice", factory));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldRemovePrototypes() {
        WebDevice device1 = new Browser(null);
        WebDevice device2 = new Browser(null);

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

    @Test
    public void shouldFormatNameInNamespace() {
        assertThat(namespace("%s-Pool", "Device"))
                .isEqualTo("webdevice.Device-Pool");
    }
}
