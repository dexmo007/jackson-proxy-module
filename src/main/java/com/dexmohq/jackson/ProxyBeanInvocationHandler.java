package com.dexmohq.jackson;

import com.dexmohq.jackson.util.PropertyAccessorsInfo;
import lombok.RequiredArgsConstructor;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * Invocation handler for JSON proxy interface backed by a map
 *
 * @author Henrik Drefs
 */
@RequiredArgsConstructor
public class ProxyBeanInvocationHandler implements InvocationHandler {

    private final Map<String, Object> data;
    private final PropertyAccessorsInfo info;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        /*
         * Delegate default interface to actual implementation
         */
        if (method.isDefault()) {
            return invokeDefaultMethod(proxy, method, args);
        }
        /*
         * Implementations for Object methods
         */
        if (method.equals(Object.class.getMethod("toString"))) {
            return data.toString();
        }
        if (method.equals(Object.class.getMethod("equals", Object.class))) {
            return areEqual(proxy, args[0]);
        }
        if (method.equals(Object.class.getMethod("hashCode"))) {
            return data.hashCode();
        }
        /*
        Property access
         */
        if (info.isGetter(method)) {
            final String propertyName = info.getPropertyName(method);
            return data.get(propertyName);
        }
        if (info.isSetter(method)) {
            data.put(info.getPropertyName(method), args[0]);
            return null;
        }
        /*
        Unknown method
         */
        throw new InvocationTargetException(new IllegalStateException("Unknown method. " +
                "Proxying only possible for default methods, equals, hashCode, toString and property accessors"));
    }

    public void setProperty(String name, Object value) {
        data.put(name, value);
    }

    /**
     * Invokes a default method on the proxy interface
     *
     * @param proxy  this proxy
     * @param method the default method
     * @param args   the arguments passed to the default method
     * @return return value from default method
     * @throws Throwable coming from default method handling internally
     */
    @SuppressWarnings("squid:S00112") // return call throws Throwable itself
    private Object invokeDefaultMethod(Object proxy, Method method, Object[] args) throws Throwable {
        final Class<?> declaringClass = method.getDeclaringClass();
        final MethodHandles.Lookup lookup = MethodHandles.lookup().in(declaringClass);
        return MethodHandles.privateLookupIn(declaringClass, lookup)
                .unreflectSpecial(method, declaringClass)
                .bindTo(proxy)
                .invokeWithArguments(args);
    }

    /**
     * Test two alleged proxy objects for equality
     *
     * @param proxy this proxy
     * @param that  the object to test for equality
     * @return true if {@code that} is equal to this {@code proxy}
     */
    private boolean areEqual(Object proxy, Object that) {
        if (that == null || proxy.getClass() != that.getClass()) {
            return false;
        }
        final InvocationHandler invocationHandler = Proxy.getInvocationHandler(that);
        if (!(invocationHandler instanceof ProxyBeanInvocationHandler)) {
            return false;
        }
        return this.data.equals(((ProxyBeanInvocationHandler) invocationHandler).data);
    }
}
