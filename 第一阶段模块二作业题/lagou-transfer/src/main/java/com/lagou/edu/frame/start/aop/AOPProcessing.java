package com.lagou.edu.frame.start.aop;

import com.lagou.edu.frame.annotation.Transactional;

public class AOPProcessing {

    Object originalBean;

    public AOPProcessing(Object originalBean) {
        this.originalBean = originalBean;
    }

    public Object Processing() {

        //需要两种做法：1.Transactional需要递归向上查找
        Object newBean = GetTopClassContainTransactionAnnotation(originalBean.getClass());
        //2.AOP需要路径判断


        return new Object();
    }

    public Object GetTopClassContainTransactionAnnotation(Class c) {
        if (c.getAnnotation(Transactional.class) != null) {
            //找到对应注解，直接代理对象并返回
            return null;
        }
        Class[] interfaces = c.getInterfaces();
        if (interfaces.length != 0) {
            for (Class anInterface : interfaces) {
                GetTopClassContainTransactionAnnotation(anInterface);
            }
        }
        Class superclass = c.getSuperclass();
        if (superclass != null) {
            GetTopClassContainTransactionAnnotation(superclass);
        }
        //没有找到对象，直接返回原对象。
        return originalBean;
    }


}
