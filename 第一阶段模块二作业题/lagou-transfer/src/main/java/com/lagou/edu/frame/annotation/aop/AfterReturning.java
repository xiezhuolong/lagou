package com.lagou.edu.frame.annotation.aop;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface AfterReturning {
    String value() default "";
}
