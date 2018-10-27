package com.dexmohq.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.Deserializers;

/**
 * @author Henrik Drefs
 */
public class ProxyModule extends Module {

    private static final Version VERSION = VersionUtil
            .parseVersion(VersionHolder.VERSION, VersionHolder.GROUP_ID, VersionHolder.ARTIFACT_ID);

    public String getModuleName() {
        return "proxy-module";
    }

    public Version version() {
        return VERSION;
    }

    public void setupModule(SetupContext context) {
        context.addDeserializers(new Deserializers.Base() {
            @SuppressWarnings("unchecked")
            @Override
            public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDesc) throws JsonMappingException {
                if (type.isInterface()) {
                    return new ProxyBeanDeserializer(type, config, beanDesc);
                }
                return super.findBeanDeserializer(type, config, beanDesc);
            }
        });
    }
}
