package com.lagou.edu.start.test;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Test1 {
    @Test
    public void Test() {
        S s = new S();
        Object proxyInstance = Proxy.newProxyInstance(S.class.getClassLoader(), S.class.getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                Object invoke = method.invoke(s, objects);
                return invoke;
            }
        });
        X x = (X) proxyInstance;
        String s1 = x.SSS("S");

        Object o = Enhancer.create(S.class, new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                Object invoke = method.invoke(s, objects);
                return invoke;
            }
        });
        S ss = (S) o;
        int iii = ss.iii(1);
        String sss = ss.SSS("sss");
    }
}
