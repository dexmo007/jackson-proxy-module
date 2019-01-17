package com.dexmohq.jackson.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.io.IOException;

/**
 * @author Henrik Drefs
 */
public class Testing {

    public static class DeserializeAsOnProperty {

        @JsonDeserialize(as = PropertyImpl.class)
        private Property foo;


        Property getFoo() {
            return foo;
        }


        void setFoo(Property foo) {
            this.foo = foo;
        }
    }

    public interface Property {
        String getName();
    }

    @Data
    public static class PropertyImpl implements Property {
        private String name;
    }

    public static void main(String[] args) throws IOException {
        final DeserializeAsOnProperty deserializeAsOnProperty = new ObjectMapper()
                .readValue("{\"foo\":{\"name\":\"bar\"}}", DeserializeAsOnProperty.class);
        System.out.println(deserializeAsOnProperty.getFoo().getClass());
        System.out.println(deserializeAsOnProperty.getFoo().getName());
    }

}
