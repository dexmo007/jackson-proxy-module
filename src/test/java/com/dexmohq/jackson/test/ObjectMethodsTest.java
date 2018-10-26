package com.dexmohq.jackson.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class ObjectMethodsTest extends TestBase {

    private static Stream<Map<String, Object>> params() {
        return Stream.of(
                Map.of(),
                Map.of("foo", "bar"),
                Map.of("foo", "bar", "num", 7)
        );
    }

    public interface Dto {
        String getFoo();

        int getNum();
    }

    @ParameterizedTest
    @MethodSource("params")
    void test(Map<String, Object> map) throws IOException {
        final String json = mapper.writeValueAsString(map);
        final Dto dto = mapper.readValue(json, Dto.class);
        final Dto dto2 = mapper.readValue(json, Dto.class);

        assertThat(dto).isEqualTo(dto2);

        assertThat(dto.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto.hashCode()).isEqualTo(map.hashCode());

        assertThat(dto.toString()).isEqualTo(dto2.toString());
        assertThat(dto.toString()).isEqualTo(map.toString());
    }

    @Test
    void testEquals() throws IOException {
        final Dto dto = mapper.readValue("{}", Dto.class);
        assertThat(dto).isNotEqualTo(null);
        assertThat(dto).isNotEqualTo(Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{Dto.class},
                (proxy, method, args) -> {
                    throw new UnsupportedOperationException();
                }));
        assertThat(dto).isNotEqualTo(Map.of());
    }
}
