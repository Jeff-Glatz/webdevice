package io.webdevice.test;


import io.bestquality.lang.CheckedRunnable;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.URL;
import java.net.URLClassLoader;

import static io.webdevice.test.MaskingClassLoader.classLoaderMasking;
import static java.util.Arrays.stream;

public class Executor {
    private ClassLoader loader;

    public Executor withClassLoader(ClassLoader loader) {
        this.loader = loader;
        return this;
    }

    public Executor withClassesIn(URL... urls) {
        return withClassLoader(new URLClassLoader(urls, getClass().getClassLoader()));
    }

    public Executor withClassesIn(Resource... resources) {
        return withClassLoader(new URLClassLoader(stream(resources)
                .map(Executor::locationOf)
                .toArray(URL[]::new),
                getClass().getClassLoader()));
    }

    public Executor withMaskedClasses(String... classes) {
        return withClassLoader(classLoaderMasking(classes));
    }

    public Executor withMaskedClasses(Class<?>... classes) {
        return withClassLoader(classLoaderMasking(classes));
    }

    public void execute(CheckedRunnable runnable)
            throws Throwable {
        CapturingExceptionHandler handler = new CapturingExceptionHandler();
        Thread executor = new Thread(runnable.asRunnable());
        executor.setContextClassLoader(loader);
        executor.setUncaughtExceptionHandler(handler);
        executor.start();
        executor.join();
        handler.assertSuccess();
    }

    private static URL locationOf(Resource resource) {
        try {
            return resource.getURL();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class CapturingExceptionHandler
            implements UncaughtExceptionHandler {
        private Throwable exception;

        @Override
        public void uncaughtException(Thread thread, Throwable exception) {
            this.exception = exception;
        }

        public void assertSuccess()
                throws Throwable {
            if (exception != null) {
                throw exception;
            }
        }
    }
}
