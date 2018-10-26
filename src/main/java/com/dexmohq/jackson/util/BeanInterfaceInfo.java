package com.dexmohq.jackson.util;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Optional;

/**
 * Provides info about a proxy interface, specifically about the properties / property accessors.
 *
 * @author Henrik Drefs
 */
@RequiredArgsConstructor
public class BeanInterfaceInfo {

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

}
