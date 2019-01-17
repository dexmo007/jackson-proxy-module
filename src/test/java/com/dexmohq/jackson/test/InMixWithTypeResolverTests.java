package com.dexmohq.jackson.test;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class InMixWithTypeResolverTests extends TestBase {

    @JsonDeserialize(as = DtoImpl.class)
    public interface DtoDeserializeAs {
        String getFoo();
    }

    @Test
    void testDeserializeAs() throws IOException {
        final DtoDeserializeAs dto = mapper.readValue("{\"foo\":\"bar\"}", DtoDeserializeAs.class);
        assertThat(dto).isInstanceOf(DtoImpl.class);
        assertThat(dto.getFoo()).isEqualTo("bar");
    }

    @Data
    public static class DtoImpl implements DtoDeserializeAs {
        private String foo;
    }


    public interface DeserializeAsOnProperty {

//        @JsonDeserialize(as = PropertyImpl.class)
        Property getFoo();
        @JsonDeserialize(as = PropertyImpl.class)
        void setFoo(Property foo);
    }

    public interface Property {
        String getName();
    }

    @Data
    public static class PropertyImpl implements Property {
        private String name;
    }

    @Test
    void testDeserializeAsOnProperty() throws IOException {
        final DeserializeAsOnProperty o = mapper.readValue("{\"foo\":{\"name\":\"bar\"}}", DeserializeAsOnProperty.class);
        assertThat(o.getFoo()).isInstanceOf(PropertyImpl.class);
        assertThat(o.getFoo().getName()).isEqualTo("bar");
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
    @JsonSubTypes(@JsonSubTypes.Type(WithJsonTypeInfoImpl.class))
    public interface WithJsonTypeInfo {
        int getFoo();
    }

    @Data
    public static class WithJsonTypeInfoImpl implements WithJsonTypeInfo {
        private int foo;
    }

    @Test
    void testWithSingleSubType() throws IOException {
        final WithJsonTypeInfo o = mapper.readValue("{" +
                "\"foo\":2," +
                "\"@class\":\"" + WithJsonTypeInfoImpl.class.getName() + "\"" +
                "}", WithJsonTypeInfo.class);
        assertThat(o).isInstanceOf(WithJsonTypeInfoImpl.class);
        assertThat(o.getFoo()).isEqualTo(2);
    }
}
