package com.dexmohq.jackson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.BeanDeserializerBuilder;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import lombok.RequiredArgsConstructor;

/**
 * @author Henrik Drefs
 */
@RequiredArgsConstructor
class ProxyBeanDeserializerModifier extends BeanDeserializerModifier {

    @Override
    public BeanDeserializerBuilder updateBuilder(DeserializationConfig config, BeanDescription beanDesc, BeanDeserializerBuilder builder) {
        if (!(builder.getValueInstantiator() instanceof ProxyValueInstantiator)) {
            return builder;
        }
        final JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(beanDesc.getBeanClass(),
                beanDesc.getClassInfo());
        for (final BeanPropertyDefinition property : beanDesc.findProperties()) {

            // builder.findProperty(property.getFullName()) != null ||
            if (ignorals.findIgnoredForDeserialization().contains(property.getName())) {
                continue;
            }
            final TypeDeserializer typeDeserializer;
            try {
                typeDeserializer = config.findTypeDeserializer(property.getPrimaryType());
            } catch (JsonMappingException e) {
                throw new IllegalStateException(e);
            }
            builder.addOrReplaceProperty(new SettableProxyProperty(property,
                    typeDeserializer
            ), true);
        }
        return builder;
    }
}
