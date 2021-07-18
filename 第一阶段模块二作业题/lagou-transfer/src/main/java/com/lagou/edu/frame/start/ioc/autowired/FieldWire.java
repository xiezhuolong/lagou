package com.lagou.edu.frame.start.ioc.autowired;

import com.lagou.edu.frame.annotation.ioc.Autowired;

import java.lang.reflect.Field;

public class FieldWire {

    TypeProcess typeProcess;

    public FieldWire(TypeProcess typeProcess) {
        this.typeProcess = typeProcess;
    }

    //需要单个Bean属性注入方法
    public Object FieldAutowired(Object o) throws Exception {
        Class<?> aClass = o.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            if (declaredField.getAnnotation(Autowired.class) != null) {
                //不存在循环依赖问题
                Object fieldValue = typeProcess.GetValueBasedOnClassTypeAndAnnotation(declaredField.getType(), declaredField.getAnnotation(Autowired.class), null);
                declaredField.set(o, fieldValue);
            }
        }
        return o;
    }
}
