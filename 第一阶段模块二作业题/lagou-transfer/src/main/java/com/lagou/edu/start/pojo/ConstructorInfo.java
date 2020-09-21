package com.lagou.edu.start.pojo;

import java.lang.reflect.Constructor;

public class ConstructorInfo {
    private Constructor<?> constructor;
    private int parameterCount;
    private boolean isUseThisAnnotation;
    private Class<?> aClass;

    public ConstructorInfo(Constructor<?> constructor, int parameterCount, boolean isUseThisAnnotation, Class<?> aClass) {
        this.constructor = constructor;
        this.parameterCount = parameterCount;
        this.isUseThisAnnotation = isUseThisAnnotation;
        this.aClass = aClass;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public int getParameterCount() {
        return parameterCount;
    }

    public boolean isUseThisAnnotation() {
        return isUseThisAnnotation;
    }

    public Class<?> getaClass() {
        return aClass;
    }

}
