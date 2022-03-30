
package com.zabava.botzabava.customaze.beanpostprocessor;

import com.zabava.botzabava.customaze.annotation.Retryable;
import com.zabava.botzabava.customaze.model.BeanData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class RetryableAnnotationBeanPostProcessor implements BeanPostProcessor {
    private final Map<String, BeanData> beanDataByBeanName = new HashMap<>();

    public RetryableAnnotationBeanPostProcessor() {
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        List<Method> methodsWithAnnotation = Optional.of(bean)
                .map(Object::getClass)
                .map(Class::getMethods)
                .stream()
                .flatMap(Arrays::stream)
                .filter(method -> method.isAnnotationPresent(Retryable.class))
                .toList();

        if (!methodsWithAnnotation.isEmpty()) {
            this.beanDataByBeanName.put(beanName, BeanData.of(bean.getClass(), methodsWithAnnotation));
        }

        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return Optional.ofNullable(beanDataByBeanName.get(beanName))
                .map(beanData -> {
                    Class<?> beanClass = beanData.originalBeanClass();
                    Map<String, Retryable> annotationsByMethodName = beanData.methods().stream()
                            .collect(Collectors.toMap(Method::getName, method -> method.getAnnotation(Retryable.class)));
                    return Proxy.newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(),
                            (proxy, method, args) -> invocationHandle(annotationsByMethodName, proxy, method, args));
                })
                .orElse(bean);
    }

    private Object invocationHandle(Map<String, Retryable> annotationsByMethodName,
                                    Object proxy,
                                    Method method,
                                    Object[] args) throws Throwable {
        String name = method.getName();
        return annotationsByMethodName.containsKey(name)
                ? addRetryableLogic(annotationsByMethodName, proxy, method, args, name)
                : method.invoke(proxy, args);
    }

    private Object addRetryableLogic(Map<String, Retryable> annotationsByMethodName,
                                     Object proxy,
                                     Method method,
                                     Object[] args,
                                     String name) throws Throwable {
        Retryable retryable = annotationsByMethodName.get(name);
        int retryCount = 0;

        while (retryable.maxAttempt() > retryCount) {
            try {
                return method.invoke(proxy, args);
            } catch (Throwable throwable) {
                retryCount++;

                if (retryable.exception().isAssignableFrom(throwable.getClass()) && retryCount < retryable.maxAttempt()) {
                    log.debug("Retryable failed, total attempt [{}]", retryCount, throwable);
                } else {
                    log.error("Retryable failed, total attempt [{}]", retryCount, throwable);
                    throw throwable;
                }
            }
        }
        return null;
    }
}
