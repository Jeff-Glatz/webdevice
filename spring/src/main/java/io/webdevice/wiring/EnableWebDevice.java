package io.webdevice.wiring;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Inherited
@Documented
@Target(TYPE)
@Retention(RUNTIME)
@Import({WebDeviceBootstrap.class})
public @interface EnableWebDevice {

    /**
     * The location of the {@link io.webdevice.settings.Settings} resource to load. These settings
     * will take precedence over settings bound from the execution environment.
     *
     * @return The location of the {@link io.webdevice.settings.Settings} resource to load.
     */
    String settings() default "";

    /**
     * Specifies the scope in which {@link io.webdevice.device.WebDevice} instances will be created.
     * <p>
     * When not explicitly configured:
     * <ul>
     *     <li>If Cucumber is on the classpath, the default value is {@code cucumber-glue}</li>
     *     <li>If Cucumber is not on the classpath, the default value is {@code webdevice}</li>
     * </ul>
     *
     * @return The scope in which {@link io.webdevice.device.WebDevice} instances will be created.
     */
    String scope() default "";

    /**
     * The name of the default device that is used when {@link io.webdevice.device.WebDevice#useDefault()}
     * is invoked. This can be the device's canonical name or any of its aliases.
     *
     * @return The name of the default device.
     */
    String defaultDevice() default "";

    /**
     * A toggle which controls whether the default device is acquired at
     * {@link io.webdevice.device.WebDevice} initialization time or not.
     *
     * @return {@code true} to acquire the default device at initialization time;
     *         {@code false} otherwise.
     */
    boolean eager() default false;

    /**
     * A toggle which controls whether an exception is raised when a new
     * device is used without releasing the previously acquired device or not.
     *
     * @return {@code true} to raise an exception when a new device is used without
     *         releasing the old one; {@code false} otherwise.
     */
    boolean strict() default true;

    /**
     * The base {@link java.net.URL} against which relative URLs will be resolved.
     *
     * @return The base URL against which relative URLs will be resolved.
     */
    String baseUrl() default "";
}
