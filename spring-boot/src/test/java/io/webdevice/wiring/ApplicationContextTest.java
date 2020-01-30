package io.webdevice.wiring;

import org.junit.Before;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

public class ApplicationContextTest {
    protected ApplicationContextRunner runner;

    @Before
    public void setUp() {
        runner = new ApplicationContextRunner();
    }

    protected ApplicationContextRunner configuredBy(Class<?>... classes) {
        return runner.withUserConfiguration(classes);
    }
}
