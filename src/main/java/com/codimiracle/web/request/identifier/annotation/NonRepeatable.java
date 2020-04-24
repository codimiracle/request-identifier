package com.codimiracle.web.request.identifier.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NonRepeatable {
    long NO_INTERVAL = -1;
    long DEFAULT_INTERVAL = 0;

    long interval() default NO_INTERVAL;
}
