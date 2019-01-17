package com.dexmohq.jackson;

import com.dexmohq.jackson.util.PropertyAccessorsInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Henrik Drefs
 */
class ProxyValueInstantiator extends ValueInstantiator.Base {
    private final BeanDescription beanDesc;

    public ProxyValueInstantiator(JavaType type, BeanDescription beanDesc) {
        super(type);
        this.beanDesc = beanDesc;
    }

//    @Override
//    public boolean canCreateFromObjectWith() {
//        return true;
//    }

    @Override
    public boolean canCreateUsingDefault() {
        return true;
    }



    @Override
    public Object createUsingDefault(DeserializationContext ctxt) throws IOException {
        try {
            return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{beanDesc.getBeanClass()},
                    new ProxyBeanInvocationHandler(new HashMap<>(), PropertyAccessorsInfo.introspect(beanDesc.getBeanClass())));
        } catch (IntrospectionException e) {
            throw new IOException(e);
        }
    }

//    @Override
//    public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
//        final JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(beanDesc.getBeanClass(),
//                beanDesc.getClassInfo());
//        final ArrayList<SettableBeanProperty> builder = new ArrayList<>();
//        for (final BeanPropertyDefinition property : beanDesc.findProperties()) {
//
//            if (ignorals.findIgnoredForDeserialization().contains(property.getName())) {
//                continue;
//            }
//            final TypeDeserializer typeDeserializer;
//            try {
//                typeDeserializer = config.findTypeDeserializer(property.getPrimaryType());
//            } catch (JsonMappingException e) {
//                throw new IllegalStateException(e);
//            }
//            builder.add(new SettableProxyProperty(property,
//                    typeDeserializer
//            ));
//        }
//        return builder.toArray(new SettableBeanProperty[0]);
//    }

//    @Override
//    public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
//        try {
//            return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
//                    new Class[]{beanDesc.getBeanClass()},
//                    new ProxyBeanInvocationHandler(new HashMap<>(), PropertyAccessorsInfo.introspect(beanDesc.getBeanClass())));
//        } catch (IntrospectionException e) {
//            throw new IOException(e);
//        }
//    }
}
