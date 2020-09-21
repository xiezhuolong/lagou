package com.lagou.edu.start.aop;

import com.lagou.edu.annotation.ioc.Autowired;
import com.lagou.edu.utils.TransactionManager;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class AOPProxy implements InvocationHandler, MethodInterceptor {
    private Object classObj;    // 原始对象

    @Autowired
    private TransactionManager transactionManager;

    public Object GetProxy(Object classObj) {
        this.classObj = classObj;
        Class<?> aClass = classObj.getClass();
        if (aClass.getInterfaces().length > 0 || aClass.isInterface()) {
            return GetJDKProxy(aClass);
        } else {
            return GetCGLibProxy(aClass);
        }
    }

    private Object GetJDKProxy(Class<?> aClass) {
        return Proxy.newProxyInstance(aClass.getClassLoader(), aClass.getInterfaces(), this);
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {
        Object result = null;
        try {
            // 开启事务(关闭事务的自动提交)
            transactionManager.beginTransaction();
            result = method.invoke(classObj, args);
            // 提交事务
            transactionManager.commit();
        } catch (Exception e) {
            e.printStackTrace();
            // 回滚事务
            transactionManager.rollback();
            // 抛出异常便于上层servlet捕获
            throw e;
        }
        return result;
    }

    private Object GetCGLibProxy(Class<?> aClass) {
        return Enhancer.create(aClass, this);
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        Object result = null;
        try {
            // 开启事务(关闭事务的自动提交)
            transactionManager.beginTransaction();
            result = method.invoke(classObj, objects);
            // 提交事务
            transactionManager.commit();
        } catch (Exception e) {
            e.printStackTrace();
            // 回滚事务
            transactionManager.rollback();
            // 抛出异常便于上层servlet捕获
            throw e;
        }
        return result;
    }
}
