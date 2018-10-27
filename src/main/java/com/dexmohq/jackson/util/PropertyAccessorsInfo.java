package com.dexmohq.jackson.util;

import lombok.RequiredArgsConstructor;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Optional;

/**
 * Provides info about a proxy interface, specifically about the properties / property accessors.
 *
 * @author Henrik Drefs
 */
@RequiredArgsConstructor
public class PropertyAccessorsInfo {

    private final HashMap<Method, String> readMethods;
    private final HashMap<Method, String> writeMethods;

    public boolean isGetter(Method method) {
        return readMethods.containsKey(method);
    }

    public boolean isSetter(Method method) {
        return writeMethods.containsKey(method);
    }

    public String getPropertyName(Method method) {
        return Optional.ofNullable(readMethods.get(method)).orElse(writeMethods.get(method));
    }

    public static PropertyAccessorsInfo introspect(Class<?> clazz) throws IntrospectionException {
        final HashMap<Method, String> readMethods = new HashMap<>();
        final HashMap<Method, String> writeMethods = new HashMap<>();
        for (PropertyDescriptor property : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
            if (property.getReadMethod() != null) {
                readMethods.put(property.getReadMethod(), property.getName());
            }
            if (property.getWriteMethod() != null) {
                writeMethods.put(property.getWriteMethod(), property.getName());
            }
        }
        return new PropertyAccessorsInfo(readMethods, writeMethods);
    }

}
