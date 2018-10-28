package com.dexmohq.jackson.test;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @author Henrik Drefs
 */
public class CustomDeserializerOnPropertyTests extends TestBase {

    public interface WithCustomDeserializerOnProperty {

        @JsonDeserialize(converter = MagicConverter.class)
        int getMagicNumber();
    }

    public static class MagicConverter implements Converter<String, Integer> {
        @Override
        public Integer convert(String value) {
            return value == null ? 0 : value.length();
        }

        @Override
        public JavaType getInputType(TypeFactory typeFactory) {
            return typeFactory.constructType(String.class);
        }

        @Override
        public JavaType getOutputType(TypeFactory typeFactory) {
            return typeFactory.constructType(Integer.class);
        }
    }

    @Test
    void testBasic() throws IOException {
        final WithCustomDeserializerOnProperty o = mapper.readValue("{\"magicNumber\":\"0815\"}", WithCustomDeserializerOnProperty.class);
        Assertions.assertThat(o.getMagicNumber()).isEqualTo(4);
    }
}
