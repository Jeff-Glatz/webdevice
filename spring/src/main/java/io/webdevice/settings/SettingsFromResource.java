package io.webdevice.settings;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.bestquality.lang.CheckedFunction;
import org.apache.commons.io.IOUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.util.ClassUtils.forName;

/**
 * This class allows the {@link io.webdevice.wiring.WebDeviceRuntime} to be configured
 * with a {@link Settings} loaded from a {@link ClassPathResource}
 */
public class SettingsFromResource
        implements CheckedFunction<String, Settings> {
    private final ConfigurableEnvironment environment;

    public SettingsFromResource(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public Settings apply(String path)
            throws Exception {
        ObjectMapper mapper = new ObjectMapper()
                .registerModule(new SimpleModule()
                        .addDeserializer(Class.class, new ClassDeserializer()));
        String content = environment.resolvePlaceholders(IOUtils.toString(
                new ClassPathResource(path).getURL(), UTF_8));
        return mapper.convertValue(mapper.readTree(content)
                .findValue("webdevice"), Settings.class);
    }

    public static class ClassDeserializer
            extends StdDeserializer<Class<?>> {

        public ClassDeserializer() {
            super(Class.class);
        }

        @Override
        public Class<?> deserialize(JsonParser parser, DeserializationContext context)
                throws IOException {
            try {
                return forName(parser.readValueAs(String.class), null);
            } catch (ClassNotFoundException e) {
                throw ValueInstantiationException.from(parser, "Failure deserializing Settings", e);
            }
        }
    }
}
