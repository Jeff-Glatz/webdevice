package io.webdevice.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.function.Predicate;

import static java.lang.Boolean.FALSE;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

public class MaskingClassLoader
        extends URLClassLoader {
    private final Predicate<String> type;
    private final Predicate<String> resource;

    public MaskingClassLoader(Predicate<String> type, Predicate<String> resource) {
        super(new URL[0], MaskingClassLoader.class.getClassLoader());
        this.type = type;
        this.resource = resource;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {
        if (type.test(name)) {
            throw new ClassNotFoundException();
        }
        return super.loadClass(name, resolve);
    }

    @Override
    public URL getResource(String name) {
        if (resource.test(name)) {
            return null;
        }
        return super.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        if (resource.test(name)) {
            return Collections.emptyEnumeration();
        }
        return super.getResources(name);
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        if (resource.test(name)) {
            return null;
        }
        return super.getResourceAsStream(name);
    }

    public static MaskingClassLoader classLoaderMasking(Class<?>... classes) {
        return new MaskingClassLoader(
                candidate -> stream(classes)
                        .map(Class::getName)
                        .anyMatch(filtered -> filtered.equals(candidate)),
                candidate -> FALSE);
    }

    public static MaskingClassLoader classLoaderMasking(String... classes) {
        return new MaskingClassLoader(
                candidate -> asList(classes).contains(candidate),
                candidate -> FALSE);
    }
}
