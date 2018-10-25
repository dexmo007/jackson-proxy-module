package com.dexmohq.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.Deserializers;

public class ProxyModule extends Module {

    public String getModuleName() {
        return "proxy-module";
    }

    public Version version() {
        return Version.unknownVersion();
    }

    public void setupModule(SetupContext context) {
        context.addDeserializers(new Deserializers.Base() {
            @SuppressWarnings("unchecked")
            @Override
            public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
                if (type.isInterface()) {
                    return new ProxyBeanDeserializer(type.getRawClass());
                }
                return super.findBeanDeserializer(type, config, beanDesc);
            }
        });
    }
}
