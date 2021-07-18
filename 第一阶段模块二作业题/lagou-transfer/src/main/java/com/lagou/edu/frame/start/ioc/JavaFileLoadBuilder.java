package com.lagou.edu.frame.start.ioc;

import com.lagou.edu.frame.annotation.ioc.Service;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.HashMap;


public class JavaFileLoadBuilder {
    String packagePath;

    //LoadClassCache loadClassCache = LoadClassCache.getInstance();
    HashMap<String, Class> hashMap = new HashMap<>();
    HashMap<String, Class> AOPHashMap = new HashMap<>();

    ClassLoader classLoader = this.getClass().getClassLoader();

    public JavaFileLoadBuilder() {
        packagePath = System.getProperty("user.dir") + "\\src\\main\\java\\";
    }

    public HashMap<String, Class> ScannerAllJavaFile(String basePackage) throws ClassNotFoundException {
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

    private void GetMatchingFiles(String packagePath) throws ClassNotFoundException {
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
                /*CrosscuttingLogic crosscuttingLogic = (CrosscuttingLogic) hashMap.get(className).getDeclaredAnnotation(CrosscuttingLogic.class);
                if (crosscuttingLogic != null) {
                    if (crosscuttingLogic.value() != "") {
                        try {
                            Class<?> aClass = classLoader.loadClass(crosscuttingLogic.value());
                        } catch (Exception e) {
                            throw new Exception("请在" + className + "上输入正确的横切逻辑BinaryName");
                        }
                    } else {
                        throw new Exception("横切逻辑类不能为空");
                    }
                }
                if (hashMap.get(className).isAnnotationPresent(CrosscuttingLogic.class)) {
                    AOPHashMap.put(className)
                }*/
                if (!hashMap.get(className).isAnnotationPresent(Service.class)/*这里可以增加判断条件，或直接使用switch语句处理，默认条件移除*/) {
                    hashMap.remove(className);
                }
            } else if (file.isDirectory()) {
                GetMatchingFiles(file.getPath());
            }
        }

    }
}
