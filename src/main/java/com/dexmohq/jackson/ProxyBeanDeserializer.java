package com.dexmohq.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Map;

public class ProxyBeanDeserializer<T> extends JsonDeserializer<T> {

    private final Class<T> type;

    public ProxyBeanDeserializer(Class<T> type) {
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Type must be an interface");
        }
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final Map<String, Object> data = p.readValueAs(new TypeReference<Map<String, Object>>() {
        });
        return PropertyUtils.proxy(type, data);
    }

}
