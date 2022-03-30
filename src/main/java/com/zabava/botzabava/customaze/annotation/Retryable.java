
package com.zabava.botzabava.customaze.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Retryable {
    int maxAttempt() default 3;

    Class<? extends Throwable> exception() default Exception.class;
}
