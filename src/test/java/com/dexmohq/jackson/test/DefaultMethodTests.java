package com.dexmohq.jackson.test;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Henrik Drefs
 */
public class DefaultMethodTests extends TestBase {

    public interface WithDefaultMethods {
        String getFoo();

        default String getFooUppercase() {
            return getFoo() == null ? null : getFoo().toUpperCase();
        }

        default String getFooOrDefault(String defaultValue) {
            return getFoo() == null ? defaultValue : getFoo();
        }
    }

    @Test
    void testSimpleDefaultMethod() throws IOException {
        final WithDefaultMethods o = mapper.readValue("{\"foo\":\"bar\"}", WithDefaultMethods.class);
        assertThat(o.getFoo()).isEqualTo("bar");
        assertThat(o.getFooUppercase()).isEqualTo("BAR");
    }

    @Test
    void testDefaultMethodWithArg() throws IOException {
        WithDefaultMethods o = mapper.readValue("{\"foo\":\"bar\"}", WithDefaultMethods.class);
        assertThat(o.getFoo()).isEqualTo("bar");
        assertThat(o.getFooOrDefault("0815")).isEqualTo("bar");
        o = mapper.readValue("{}", WithDefaultMethods.class);
        assertThat(o.getFooOrDefault("0815")).isEqualTo("0815");
    }
}
