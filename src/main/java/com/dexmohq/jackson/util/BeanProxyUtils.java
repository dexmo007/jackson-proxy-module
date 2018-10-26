package com.dexmohq.jackson.util;

import com.dexmohq.jackson.ProxyBeanInvocationHandler;
import lombok.experimental.UtilityClass;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class BeanProxyUtils {

    /**
     * Create a proxy for a given interface, for a given list of properties and a backing data map
     *
     * @param interfaceClass the proxy interface
     * @param data           the backing map
     * @param <T>            type of the proxy interface
     * @return the created proxy instance
     */
    public static <T> T proxy(Class<T> interfaceClass, Map<String, Object> data) throws IOException {
        if (!interfaceClass.isInterface()) {
            throw new IllegalArgumentException("class must be an interface");
        }
        final HashMap<Method, String> readMethods = new HashMap<>();
        final HashMap<Method, String> writeMethods = new HashMap<>();
        try {
            for (PropertyDescriptor property : Introspector.getBeanInfo(interfaceClass).getPropertyDescriptors()) {
                if (property.getReadMethod() != null) {
                    readMethods.put(property.getReadMethod(), property.getName());
                }
                if (property.getWriteMethod() != null) {
                    writeMethods.put(property.getWriteMethod(), property.getName());
                }
            }
        } catch (IntrospectionException e) {
            throw new IOException(e);
        }
        final BeanInterfaceInfo info = new BeanInterfaceInfo(readMethods, writeMethods);
        @SuppressWarnings("unchecked") final T proxy = (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[]{interfaceClass},
                new ProxyBeanInvocationHandler(data, info));
        return proxy;
    }
}
