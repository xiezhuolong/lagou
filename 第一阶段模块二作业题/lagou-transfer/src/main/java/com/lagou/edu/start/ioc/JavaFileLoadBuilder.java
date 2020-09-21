package com.lagou.edu.start.ioc;

import com.lagou.edu.annotation.ioc.Service;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.HashMap;


public class JavaFileLoadBuilder {
    String packagePath;

    //LoadClassCache loadClassCache = LoadClassCache.getInstance();
    HashMap<String, Class> hashMap = new HashMap<>();
    HashMap<String, Class> AOPHashMap = new HashMap<>();

    ClassLoader classLoader = this.getClass().getClassLoader();

    public HashMap<String, Class> ScannerAllJavaFile(String basePackage) throws ClassNotFoundException {
        //拿到当前代码目录
        packagePath = System.getProperty("user.dir") + "\\src\\main\\java\\";
        //如果有指定目录则拼接
        if (!basePackage.isEmpty()) {
            packagePath += basePackage.replace(".", "\\").replace("*", "");
        }
        GetMatchingFiles(packagePath, basePackage);
        return hashMap;
    }

    private void GetMatchingFiles(String packagePath, String basePackage) throws ClassNotFoundException {
        File[] files = new File(packagePath).listFiles();
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(".java")) {
                //这里可以修改成允许自定义Bean名称，默认类名。如果使用的是注解，删除当前类名，放入自定义类名
                String className = file.getName().substring(0, file.getName().length() - 5);
                String referenceName = file.getPath().substring(this.packagePath.length(), file.getPath().length() - 5).replace("\\", ".");
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
                GetMatchingFiles(file.getPath(), basePackage);
            }
        }

    }
}
