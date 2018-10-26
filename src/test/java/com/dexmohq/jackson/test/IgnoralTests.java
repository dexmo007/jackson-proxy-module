package com.dexmohq.jackson.test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class IgnoralTests extends TestBase {

    public interface DtoJsonIgnoreOnGetter {
        @JsonIgnore
        String getFoo();
    }

    @Test
    void testJsonIgnoreOnGetter() throws IOException {
        final DtoJsonIgnoreOnGetter dto = mapper.readValue("{\"foo\": [1,2,3]}", DtoJsonIgnoreOnGetter.class);
        Assertions.assertThat(dto.getFoo()).isNull();
    }

    public interface DtoJsonIgnoreOnSetter {
        String getFoo();

        @JsonIgnore
        void setFoo(String foo);
    }

    @Test
    void testJsonIgnoreOnSetter() throws IOException {
        final DtoJsonIgnoreOnSetter dto = mapper.readValue("{\"foo\": [1,2,3]}", DtoJsonIgnoreOnSetter.class);
        Assertions.assertThat(dto.getFoo()).isNull();
    }

    @JsonIgnoreProperties("foo")
    public interface DtoJsonIgnoreProperties {
        String getFoo();
    }

    @Test
    void testJsonIgnoreProperties() throws IOException {
        final DtoJsonIgnoreProperties dto = mapper.readValue("{\"foo\": [1,2,3]}", DtoJsonIgnoreProperties.class);
        Assertions.assertThat(dto.getFoo()).isNull();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public interface DtoIgnoreUnknownAnnotated {
        String getFoo();
    }

    @Test
    void testIgnoreUnknownAnnotated() throws IOException {
        final DtoIgnoreUnknownAnnotated dto = mapper.readValue("{\"foo\": \"bar\", \"unknown\": 0}", DtoIgnoreUnknownAnnotated.class);
        Assertions.assertThat(dto.getFoo()).isEqualTo("bar");
    }

    public interface DtoIgnoreUnknownByFeature {
        String getFoo();
    }

    @Test
    void testIgnoreUnknownByFeature() throws IOException {
        final DtoIgnoreUnknownByFeature dto = mapper
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("{\"foo\": \"bar\", \"unknown\": 0}", DtoIgnoreUnknownByFeature.class);
        Assertions.assertThat(dto.getFoo()).isEqualTo("bar");
    }

}
