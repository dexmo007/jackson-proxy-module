package com.dexmohq.jackson.test;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.util.StdConverter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Henrik Drefs
 */
public class JsonDeserializeTests extends TestBase {

    public static class MagicConverter extends StdConverter<String, Integer> {
        @Override
        public Integer convert(String value) {
            return value == null ? 0 : value.length();
        }
    }

    public interface WithConverter {

        @JsonDeserialize(converter = MagicConverter.class)
        int getMagicNumber();
    }

    @Test
    void testConverter() throws IOException {
        final WithConverter o = mapper.readValue("{\"magicNumber\":\"0815\"}", WithConverter.class);
        assertThat(o.getMagicNumber()).isEqualTo(4);
    }

    public interface WithContentConverter {

        @JsonDeserialize(contentConverter = MagicConverter.class)
        List<Integer> getMagicNumber();
    }

    @Test
    void testContentConverter() throws IOException {
        final WithContentConverter o = mapper.readValue("{\"magicNumber\":[\"0815\",\"bar\"]}", WithContentConverter.class);
        assertThat(o.getMagicNumber()).containsExactly(4, 3);
    }

    public interface WithDeserializeUsing {

        String getFoo();
        @JsonDeserialize(using = FooDeserializer.class)
        void setFoo(String foo);
    }

    public static class FooDeserializer extends StdDeserializer<String> {

        public FooDeserializer() {
            super(String.class);
        }

        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return "<" + p.readValueAs(String.class) + ">";
        }
    }

    @Test
    void testDeserializeUsing() throws IOException {
        final WithDeserializeUsing o = mapper.readValue("{\"foo\":\"bar\"}", WithDeserializeUsing.class);
        assertThat(o.getFoo()).isEqualTo("<bar>");
    }
}
