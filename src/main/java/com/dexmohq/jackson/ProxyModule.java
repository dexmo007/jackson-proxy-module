package com.dexmohq.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.util.VersionUtil;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.ValueInstantiators;

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
        context.addBeanDeserializerModifier(new ProxyBeanDeserializerModifier());
        context.addValueInstantiators(new ValueInstantiators.Base() {
            @Override
            public ValueInstantiator findValueInstantiator(DeserializationConfig config, BeanDescription beanDesc, ValueInstantiator defaultInstantiator) {
                if (defaultInstantiator != null && defaultInstantiator.canInstantiate()) {
                    return defaultInstantiator;
                }
                if (!beanDesc.getBeanClass().isInterface()) {
                    return defaultInstantiator;
                }
                return new ProxyValueInstantiator(beanDesc);
            }
        });
    }
}
