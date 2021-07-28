package com.lagou.edu.config;

import com.lagou.edu.frame.annotation.aop.*;

@CrosscuttingLogic
public class AOPConfig {

    @LogicRange("com.lagou.edu.*")
    public void lr1(){}


    @Before("lr1()")
    public void beforeMethod(JoinPoint joinPoint) {
        //JoinPoint 需要实现3个方法，1.获取传入参数，2.获取原始目标类，3.获取上级调用类
        System.out.println("Before Method");
    }

    @AfterReturning("pt1()")
    public void arMethod(JoinPoint joinPoint) {
        System.out.println("AfterReturning Method");
    }

    @AfterThrowing("pt1()")
    public void atMethod(JoinPoint joinPoint) {
        System.out.println("AfterThrowing Method");
    }

    @After("pt1()")
    public void afterMethod(JoinPoint joinPoint) {
        System.out.println("After Method");
    }

    @Around("pt1()")
    public void aroundMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        //ProceedingJoinPoint 继承自JoinPoint并增加proceed()和proceed(Object[] args)方法用来执行和修改入参，数量对不上就保错
        System.out.println("Around Method proceed before");
        proceedingJoinPoint.proceed();
        System.out.println("Around Method proceed after");
    }
}
