
package com.zabava.botzabava.customaze.model;

import java.lang.reflect.Method;
import java.util.Collection;

public record BeanData(Class<?> originalBeanClass, Collection<Method> methods) {
    public static BeanData of(Class<?> aClass, Collection<Method> methods) {
        return new BeanData(aClass, methods);
    }
}
