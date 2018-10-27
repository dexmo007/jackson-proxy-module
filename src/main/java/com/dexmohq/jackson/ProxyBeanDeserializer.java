package com.dexmohq.jackson;

import com.dexmohq.jackson.util.PropertyAccessorsInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * Deserializes an interface type by proxying. Fields are deserialized recursively.
 *
 * @param <T> the interface type to deserialize
 * @author Henrik Drefs
 */
public class ProxyBeanDeserializer<T> extends JsonDeserializer<T> {

    private final Class<T> type;
    private final BeanDescription beanDescription;
    private JsonIgnoreProperties.Value ignorals;

    @SuppressWarnings("unchecked")
    public ProxyBeanDeserializer(JavaType type, DeserializationConfig config, BeanDescription beanDescription) {
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Type must be an interface");
        }
        this.type = (Class<T>) type.getRawClass();
        this.beanDescription = beanDescription;
        this.ignorals = config.getDefaultPropertyIgnorals(beanDescription.getBeanClass(),
                beanDescription.getClassInfo());
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final TreeNode treeNode = p.readValueAsTree();
        if (!treeNode.isObject()) {
            throw MismatchedInputException.from(p, type, "Expecting object");
        }

        final HashMap<String, BeanPropertyDefinition> propertyTypes = new HashMap<>();

        final List<BeanPropertyDefinition> properties = beanDescription.findProperties();
        for (BeanPropertyDefinition property : properties) {
            propertyTypes.put(property.getName(), property);
        }
        final HashMap<String, Object> data = new HashMap<>();
        final Iterator<Entry<String, JsonNode>> elements = ((ObjectNode) treeNode).fields();
        while (elements.hasNext()) {
            final Entry<String, JsonNode> field = elements.next();
            final String fieldName = field.getKey();
            final JsonNode node = field.getValue();
            final BeanPropertyDefinition propertyDef = propertyTypes.get(fieldName);
            if (isIgnoredProperty(p, ctxt, fieldName, propertyDef)) {
                continue;
            }
            assert propertyDef != null;// if it is null and not ignored, an exception is thrown by ctxt.handleUnknownProperty(..)
            final ObjectCodec codec = p.getCodec();
            final JsonParser subParser = codec.treeAsTokens(node);
            final Object propertyValue = codec.readValue(subParser, propertyDef.getPrimaryType());
            data.put(propertyDef.getInternalName(), propertyValue);
        }
        final PropertyAccessorsInfo info;
        try {
            info = PropertyAccessorsInfo.introspect(type);
        } catch (IntrospectionException e) {
            throw InvalidDefinitionException.from(p, "Error during proxy interface introspection", e);
        }
        @SuppressWarnings("unchecked") final T proxy = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{type},
                new ProxyBeanInvocationHandler(data, info));
        return proxy;
    }

    /**
     * Checks if a given property should be ignored.
     * <p>
     * As a side-effect this method will throw an exception if the given property is unknown
     * and the {@link DeserializationFeature} {@code FAIL_ON_UNKNOWN_PROPERTIES} is enabled.
     *
     * @param p           the current parser
     * @param ctxt        the current deserialization context
     * @param fieldName   the name of the parsed (JSON) field
     * @param propertyDef the property definition of the field or null if the field is unknown
     * @return true if the property should be skipped
     * @throws IOException if an unknown property occurs and the {@code FAIL_ON_UNKNOWN_PROPERTIES} feature is enabled
     * @see DeserializationContext#handleUnknownProperty(JsonParser, JsonDeserializer, Object, String)
     * @see DeserializationFeature#FAIL_ON_UNKNOWN_PROPERTIES
     */
    private boolean isIgnoredProperty(JsonParser p, DeserializationContext ctxt, String fieldName, BeanPropertyDefinition propertyDef) throws IOException {
        if (propertyDef != null) {
            return beanDescription.getIgnoredPropertyNames().contains(fieldName)
                    || ignorals.findIgnoredForDeserialization().contains(fieldName);
        }
        return ignorals.getIgnoreUnknown()
                || beanDescription.getIgnoredPropertyNames().contains(fieldName)
                || ctxt.handleUnknownProperty(p, this, type, fieldName);
    }


}
