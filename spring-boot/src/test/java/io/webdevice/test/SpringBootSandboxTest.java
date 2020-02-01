package io.webdevice.test;

import org.junit.Before;

public abstract class SpringBootSandboxTest {
    private SpringBootSandbox sandbox;

    @Before
    public void setUp() {
        sandbox = new SpringBootSandbox();
    }

    public SpringBootSandbox sandbox() {
        return sandbox;
    }
}
