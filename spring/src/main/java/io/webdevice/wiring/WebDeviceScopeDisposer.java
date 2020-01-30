package io.webdevice.wiring;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import static io.webdevice.wiring.WebDeviceScope.scope;
import static java.lang.Boolean.TRUE;
import static org.springframework.test.context.support.DependencyInjectionTestExecutionListener.REINJECT_DEPENDENCIES_ATTRIBUTE;

public class WebDeviceScopeDisposer
        extends AbstractTestExecutionListener {

    @Override
    public void afterTestMethod(TestContext context) {
        WebDeviceScope scope = scope(context.getApplicationContext());
        if (scope != null && scope.dispose()) {
            context.setAttribute(REINJECT_DEPENDENCIES_ATTRIBUTE, TRUE);
        }
    }

    @Override
    public void afterTestClass(TestContext context) {
        WebDeviceScope scope = scope(context.getApplicationContext());
        if (scope != null && !scope.isEmpty()) {
            scope.dispose();
        }
    }
}
