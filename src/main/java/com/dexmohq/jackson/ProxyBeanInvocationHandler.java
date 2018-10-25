package com.dexmohq.jackson;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class ProxyBeanInvocationHandler implements InvocationHandler {

    private final Map<String, Object> data;

    public ProxyBeanInvocationHandler(Map<String, Object> data) {
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.isDefault()) {
            /*
            Delegate default interface to actual implementation
             */
            return MethodHandles.privateLookupIn(method.getDeclaringClass(),
                    MethodHandles.lookup().in(method.getDeclaringClass()))
                    .unreflectSpecial(method, method.getDeclaringClass())
                    .bindTo(proxy)
                    .invokeWithArguments(args);
        }
        final String propertyName = PropertyUtils.getPropertyName(method);
        if (PropertyUtils.isGetter(method)) {
            final Object value = data.get(propertyName);
            if (method.getReturnType().isInterface()) {
                return PropertyUtils.proxy(method.getReturnType(), (Map<String, Object>) value);
            }
            return value;
        }
        if (PropertyUtils.isSetter(method)) {
            data.put(propertyName, args[0]);
            return null;
        }
        throw new InvocationTargetException(new IllegalStateException("proxying not possible"));
    }
}
