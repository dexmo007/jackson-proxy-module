package com.dexmohq.jackson;

import com.dexmohq.jackson.util.BeanProxyUtils;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
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

    public ProxyBeanDeserializer(Class<T> type) {
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Type must be an interface");
        }
        this.type = type;
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        final TreeNode treeNode = p.readValueAsTree();
        if (!treeNode.isObject()) {
            throw ctxt.wrongTokenException(p, type, JsonToken.START_OBJECT, "proxying only viable for json objects");
        }

        final HashMap<String, BeanPropertyDefinition> propertyTypes = new HashMap<>();

        final BeanDescription desc = ctxt.getConfig().introspect(ctxt.constructType(type));
        final List<BeanPropertyDefinition> properties = desc.findProperties();
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
            if (isIgnoredProperty(p, ctxt, desc, fieldName, propertyDef)) {
                continue;
            }
            final Object propertyValue = p.getCodec().readValue(p.getCodec().treeAsTokens(node), propertyDef.getPrimaryType());
            data.put(propertyDef.getInternalName(), propertyValue);
        }
        return BeanProxyUtils.proxy(type, data);
    }

    private boolean isIgnoredProperty(JsonParser p, DeserializationContext ctxt, BeanDescription desc, String fieldName, BeanPropertyDefinition propertyDef) throws IOException {
        final JsonIgnoreProperties.Value ignorals = ctxt.getConfig()
                .getDefaultPropertyIgnorals(desc.getBeanClass(),
                        desc.getClassInfo());
        if (propertyDef == null) {
            if (ignorals.getIgnoreUnknown()
                    || desc.getIgnoredPropertyNames().contains(fieldName)
                    || ctxt.handleUnknownProperty(p, this, type, fieldName)) {
                return true;
            } else {
                throw new InternalError("cannot happen");
            }
        }
        return desc.getIgnoredPropertyNames().contains(fieldName)
                || ignorals.findIgnoredForDeserialization().contains(fieldName);
    }


}
