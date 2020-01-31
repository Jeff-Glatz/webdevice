package io.webdevice.wiring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import static io.webdevice.wiring.WebDeviceScope.scope;
import static java.lang.Boolean.TRUE;
import static org.springframework.test.context.support.DependencyInjectionTestExecutionListener.REINJECT_DEPENDENCIES_ATTRIBUTE;

public class WebDeviceScopeDisposer
        extends AbstractTestExecutionListener {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void afterTestMethod(TestContext context) {
        WebDeviceScope scope = scope(context.getApplicationContext());
        if (scope != null && scope.dispose()) {
            log.debug("Disposed webdevice scope after test method: {}", context.getTestMethod());
            context.setAttribute(REINJECT_DEPENDENCIES_ATTRIBUTE, TRUE);
        }
    }

    @Override
    public void afterTestClass(TestContext context) {
        WebDeviceScope scope = scope(context.getApplicationContext());
        if (scope != null && !scope.isEmpty()) {
            log.info("Disposing webdevice scope after test class: {}", context.getTestClass());
            scope.dispose();
        }
    }
}
