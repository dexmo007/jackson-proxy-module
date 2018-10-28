package com.dexmohq.jackson;

import com.dexmohq.jackson.util.PropertyAccessorsInfo;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import lombok.RequiredArgsConstructor;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.HashMap;

/**
 * @author Henrik Drefs
 */
@RequiredArgsConstructor
class ProxyValueInstantiator extends ValueInstantiator {
    private final BeanDescription beanDesc;

    @Override
    public boolean canCreateFromObjectWith() {
        return true;
    }

    @Override
    public SettableBeanProperty[] getFromObjectArguments(DeserializationConfig config) {
        return new SettableBeanProperty[0];
    }

    @Override
    public Object createFromObjectWith(DeserializationContext ctxt, Object[] args) throws IOException {
        try {
            return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                    new Class[]{beanDesc.getBeanClass()},
                    new ProxyBeanInvocationHandler(new HashMap<>(), PropertyAccessorsInfo.introspect(beanDesc.getBeanClass())));
        } catch (IntrospectionException e) {
            throw new IOException(e);
        }
    }
}
