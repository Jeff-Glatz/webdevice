package io.webdevice.settings;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.bestquality.lang.CheckedFunction;
import org.apache.commons.io.IOUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ClassUtils;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class SettingsFromResource
        implements CheckedFunction<String, Settings> {
    private final ConfigurableEnvironment environment;

    public SettingsFromResource(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @Override
    public Settings apply(String path)
            throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new SimpleModule()
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
                return ClassUtils.forName(parser.readValueAs(String.class), null);
            } catch (ClassNotFoundException e) {
                throw new IOException(e);
            }
        }
    }
}
