package com.lagou.edu.frame.start.ioc;

import com.lagou.edu.frame.start.pojo.ConstructorInfo;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

public class ConstructorSet {
    private Set<ConstructorInfo> constructorInfos = new HashSet<>();

    //1.加入元素到Set
    public boolean Add(ConstructorInfo constructorInfo) {
        return constructorInfos.add(constructorInfo);
    }

    public boolean Add(Constructor constructor, int parameterCount, boolean isUseThisAnnotation, Class<?> aClass) {
        return Add(new ConstructorInfo(constructor, parameterCount, isUseThisAnnotation, aClass));
    }

    //2.清除Set元素
    public void Clear() {
        constructorInfos.clear();
    }

    //3.计算得到需要初始化的Constructor
    //1）判断有无@UseThisConstructor注解，如果有确定该类只有唯一一个否则抛异常
    //2）如果没有@UseThisConstructor注解，则返回构造函参数数最多的构造函数
    public Constructor GetInitConstructor() throws Exception {
        boolean b = false;
        Constructor returnConstructor = null;
        for (ConstructorInfo constructorInfo : constructorInfos) {
            if (b && constructorInfo.isUseThisAnnotation()) {
                throw new Exception(constructorInfo.getaClass().getName() + "类中存在多个使用@UseThisConstructor注解的构造函数");
            } else if (constructorInfo.isUseThisAnnotation()) {
                b = constructorInfo.isUseThisAnnotation();  // b = true
                returnConstructor = constructorInfo.getConstructor();
            }
            if (!b) {
                if (returnConstructor != null) {
                    if (returnConstructor.getParameterCount() < constructorInfo.getParameterCount()) {
                        returnConstructor = constructorInfo.getConstructor();
                    }
                } else {
                    returnConstructor = constructorInfo.getConstructor();
                }
            }
        }
        return returnConstructor;
    }
}
