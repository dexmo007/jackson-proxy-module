package com.dexmohq.jackson;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

public final class PropertyUtils {


    static String getPropertyName(Method method) {
        final String methodName = method.getName();
        return Introspector.decapitalize(methodName.substring(methodName.startsWith("is") ? 2 : 3));
    }

    static boolean isGetter(Method method) {
        final String methodName = method.getName();
        return (methodName.startsWith("is") || methodName.startsWith("get")) && method.getParameterCount() == 0
                && method.getReturnType() != Void.TYPE;
    }

    static boolean isSetter(Method method) {
        final String methodName = method.getName();
        return methodName.startsWith("set") && method.getParameterCount() == 1 && method.getReturnType() == Void.TYPE;
    }

    @SuppressWarnings("unchecked")
    static <T> T proxy(Class<T> interfaceClass, Map<String, Object> data) {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{interfaceClass},
                new ProxyBeanInvocationHandler(data));
    }
}
