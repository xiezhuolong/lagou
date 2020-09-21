package com.lagou.edu.start.ioc.autowired;

import com.lagou.edu.annotation.ioc.Autowired;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;

public class MethodWired {
    TypeProcess typeProcess;

    public MethodWired(TypeProcess typeProcess) {
        this.typeProcess = typeProcess;
    }

    public Object MethodAutowired(Object o) throws Exception {
        Object returnValue = null;
        for (Method declaredMethod : o.getClass().getDeclaredMethods()) {
            if (declaredMethod.isAnnotationPresent(Autowired.class)) {
                returnValue = InitMethod(declaredMethod, o);
            }
        }
        return returnValue;
    }

    private Object InitMethod(Method method, Object o) throws Exception {
        ArrayList<Object> objects = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            //根据参数获取对应类型的值
            Object parameterValue = typeProcess.GetValueBasedOnClassTypeAndAnnotation(parameter.getType(), parameter.getAnnotation(Autowired.class), null);
            objects.add(parameterValue);
        }
        Object returnValue = method.invoke(o, objects.toArray());
        return returnValue;
    }
}
