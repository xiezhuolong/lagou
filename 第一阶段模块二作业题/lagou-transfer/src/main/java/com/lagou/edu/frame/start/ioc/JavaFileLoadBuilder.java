package com.lagou.edu.frame.start.ioc;

import com.lagou.edu.frame.annotation.aop.CrosscuttingLogic;
import com.lagou.edu.frame.annotation.ioc.Service;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;


public class JavaFileLoadBuilder {
    String packagePath;

    //LoadClassCache loadClassCache = LoadClassCache.getInstance();
    HashMap<String, Class> hashMap = new HashMap<>();
    //增强分为5类、环绕、前置、后置、后置返回或后置异常，但每类多少个还不知道，数据格式需要再设计一下，可以参考IdentityHashMap
    HashMap<String, ArrayList<String>> AOPHashMap = new HashMap<>(5);

    ClassLoader classLoader = this.getClass().getClassLoader();

    public JavaFileLoadBuilder() {
        packagePath = System.getProperty("user.dir") + "\\src\\main\\java\\";
        AOPHashMap.put("myAround", new ArrayList<>());
        AOPHashMap.put("myBefore", new ArrayList<>());
        AOPHashMap.put("myAfter", new ArrayList<>());
        AOPHashMap.put("myAfterReturning", new ArrayList<>());
        AOPHashMap.put("myAfterThrowing", new ArrayList<>());
    }

    public HashMap<String, Class> ScannerAllJavaFile(String basePackage) throws Exception {
        //拿到当前代码目录
        //如果有指定目录则拼接
        if (basePackage.isEmpty()) {
            basePackage = packagePath;
        } else {
            basePackage = packagePath + basePackage.replace(".", "\\").replace("*", "");
        }
        GetMatchingFiles(basePackage);
        return hashMap;
    }

    private void GetMatchingFiles(String packagePath) throws Exception {
        File[] files = new File(packagePath).listFiles();
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".java")) {
                //这里可以修改成允许自定义Bean名称，默认类名。如果使用的是注解，删除当前类名，放入自定义类名
                String className = file.getName().substring(0, file.getName().length() - 5);
                //如果 basePackage 不等于空，下面这行会不会有问题？
                //会有，已修复
                String referenceName = file.getPath().substring(this.packagePath.length(), file.getPath().length() - 5).replace("\\", ".");
                ClassLoader.getPlatformClassLoader().getParent();
                System.out.println(className);
                hashMap.put(className, classLoader.loadClass(referenceName));
                Annotation[] declaredAnnotations = hashMap.get(className).getDeclaredAnnotations();
                Annotation[] annotations = hashMap.get(className).getAnnotations();
                //需要在做AOP路径判断整合以路径为KEY，AOP类型为VALUE
                CrosscuttingLogic crosscuttingLogic = (CrosscuttingLogic) hashMap.get(className).getDeclaredAnnotation(CrosscuttingLogic.class);
                if (crosscuttingLogic != null) {
                    //取类里面所有增强注解与对应pointCut加入AOPHashMap中

                }
                if (hashMap.get(className).isAnnotationPresent(CrosscuttingLogic.class)) {
                    AOPHashMap.put(className, this.getClass());
                }
                if (!hashMap.get(className).isAnnotationPresent(Service.class)/*这里可以增加判断条件，或直接使用switch语句处理，默认条件移除*/) {
                    hashMap.remove(className);
                }
            } else if (file.isDirectory()) {
                GetMatchingFiles(file.getPath());
            }
        }

    }
}
