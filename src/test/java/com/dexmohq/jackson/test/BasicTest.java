package com.dexmohq.jackson.test;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BasicTest extends TestBase {

    public interface FooDto {
        String getName();

        default String getNameUppercase() {
            return getName().toUpperCase();
        }

        void setName(String name);

        void bullshit();

        BarDto getBar();
    }

    public interface BarDto {
        int getNumber();
    }

    public static class FooDtoImpl implements FooDto {

        private String name;
        private int i;

        public FooDtoImpl(String name, int i) {
            this.name = name;
            this.i = i;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }

        @Override
        public void bullshit() {

        }

        @Override
        public BarDto getBar() {
            return () -> i;
        }

    }

    @Test
    void testFooDto() throws IOException {
        final FooDtoImpl bean = new FooDtoImpl("bar", 1);
        final String json = mapper.writeValueAsString(bean);
        final FooDto fooDto = mapper.readValue(json, FooDto.class);
        assertThat(Proxy.isProxyClass(fooDto.getClass())).isTrue();
        assertEquals("bar", fooDto.getName());
        assertEquals("BAR", fooDto.getNameUppercase());
        assertThrows(UndeclaredThrowableException.class, fooDto::bullshit);
        assertThatThrownBy(fooDto::bullshit)
                .hasCauseInstanceOf(InvocationTargetException.class);

        fooDto.setName("another");
        assertThat(fooDto.getName()).isEqualTo("another");

        assertThat(fooDto.getBar()).isNotNull();
        assertThat(fooDto.getBar()).satisfies(bar -> assertThat(Proxy.isProxyClass(bar.getClass())).isTrue());
        assertThat(fooDto.getBar().getNumber()).isEqualTo(1);
    }
}
