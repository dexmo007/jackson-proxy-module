package com.dexmohq.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.deser.NullValueProvider;
import com.fasterxml.jackson.databind.deser.SettableBeanProperty;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;

/**
 * @author Henrik Drefs
 */
class SettableProxyProperty extends SettableBeanProperty {

    private final BeanPropertyDefinition propDef;
    private final boolean _skipNulls = false;// todo retrieve this flag from config

    protected SettableProxyProperty(BeanPropertyDefinition propDef, TypeDeserializer typeDeser) {
        super(propDef, propDef.getPrimaryType(), typeDeser, propDef.getGetter().getAllAnnotations());
        this.propDef = propDef;
    }

    public SettableProxyProperty(BeanPropertyDefinition propDef, JsonDeserializer<Object> valueDeser) {
        super(propDef.getFullName(), propDef.getPrimaryType(), propDef.getMetadata(), valueDeser);
        this.propDef = propDef;
    }

    @Override
    public SettableBeanProperty withValueDeserializer(JsonDeserializer<?> deser) {
        return new SettableProxyProperty(propDef, (JsonDeserializer<Object>) deser);
    }

    @Override
    public SettableBeanProperty withName(PropertyName newName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SettableBeanProperty withNullProvider(NullValueProvider nva) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AnnotatedMember getMember() {
        return propDef.getGetter();
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> acls) {
        return propDef.getGetter().getAnnotation(acls);
    }

    @Override
    public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
        Object value;
        if (p.hasToken(JsonToken.VALUE_NULL)) {
            if (_skipNulls) {
                return;
            }
            value = _nullProvider.getNullValue(ctxt);
        } else if (_valueTypeDeserializer == null) {
            value = _valueDeserializer.deserialize(p, ctxt);
            // 04-May-2018, tatu: [databind#2023] Coercion from String (mostly) can give null
            if (value == null) {
                if (_skipNulls) {
                    return;
                }
                value = _nullProvider.getNullValue(ctxt);
            }
        } else {
            value = _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer);
        }
        try {
            set(instance, value);
        } catch (Exception e) {
            _throwAsIOE(p, e, value);
        }
    }

    @Override
    public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance) throws IOException {
        Object value;
        if (p.hasToken(JsonToken.VALUE_NULL)) {
            if (_skipNulls) {
                return instance;
            }
            value = _nullProvider.getNullValue(ctxt);
        } else if (_valueTypeDeserializer == null) {
            value = _valueDeserializer.deserialize(p, ctxt);
            // 04-May-2018, tatu: [databind#2023] Coercion from String (mostly) can give null
            if (value == null) {
                if (_skipNulls) {
                    return instance;
                }
                value = _nullProvider.getNullValue(ctxt);
            }
        } else {
            value = _valueDeserializer.deserializeWithType(p, ctxt, _valueTypeDeserializer);
        }
        try {
            set(instance, value);
        } catch (Exception e) {
            _throwAsIOE(p, e, value);
        }
        return instance;
    }

    @Override
    public void set(Object instance, Object value) throws IOException {
        ((ProxyBeanInvocationHandler) Proxy.getInvocationHandler(instance)).setProperty(propDef.getInternalName(), value);
    }

    @Override
    public Object setAndReturn(Object instance, Object value) throws IOException {
        set(instance, value);
        return null;
    }
}
