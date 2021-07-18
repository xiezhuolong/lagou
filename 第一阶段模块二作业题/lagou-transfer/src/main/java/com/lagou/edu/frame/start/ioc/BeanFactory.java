package com.lagou.edu.frame.start.ioc;

import com.lagou.edu.frame.annotation.ioc.Autowired;
import com.lagou.edu.frame.annotation.ioc.UseThisConstructor;
import com.lagou.edu.frame.start.aop.AOPProcessing;
import com.lagou.edu.frame.start.ioc.autowired.FieldWire;
import com.lagou.edu.frame.start.ioc.autowired.MethodWired;
import com.lagou.edu.frame.start.ioc.autowired.TypeProcess;
import com.lagou.edu.frame.start.pojo.BeanCache;
import com.lagou.edu.frame.start.pojo.InitializedClassCache;
import com.lagou.edu.frame.start.pojo.LoadClassCache;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BeanFactory {
    //一级缓存
    LoadClassCache loadClassCache = LoadClassCache.getInstance();
    //二级缓存
    InitializedClassCache initializedClassCache = InitializedClassCache.getInstance();
    //三级缓存
    ConcurrentHashMap<String, Object> beanCache = BeanCache.getBeanCache();
    //记录正在创建的Bean，判断循环依赖
    List<String> beansInCreation = new ArrayList<>();

    TypeProcess typeProcess = new TypeProcess(this);

    FieldWire fieldWire = new FieldWire(typeProcess);

    MethodWired methodWired = new MethodWired(typeProcess);

    //2.初始化过程中需要对注解进行读取，对需要注入的属性进行注入（注解嵌套情况处理）
    //1）同时如果是通过有参构造函数进行初始化的话需要找到依赖的参数类注入（如果构造函数循环依赖则抛出异常
    //2）初始化时还需判断是单纯接口还是单独的类，根据情况选择不需要代理类还是JDK或CGLib动态代理生成代理类
    public void InitOriginLoadClassBean(HashMap<String, Class> originLoadClass) throws Exception {
        for (Map.Entry<String, Class> stringClassEntry : originLoadClass.entrySet()) {
            InitBean(stringClassEntry.getKey(), stringClassEntry.getValue());
        }
    }

    //需要初始化单个Bean方法
    public Object InitBean(String key, Class value) throws Exception {
        //先判断Bean有没有被创建，可能在解决循环依赖的时候后面的Bean被先创建了
        //为了避免重复创建，做此优化。
        Object bean = GetBean(key, null, null);
        if (bean != null) {
            return bean;
        }
        //加入正在创建中
        beansInCreation.add(key);
        //1.获取正确的初始化构造器
        Constructor constructor = GetInitConstructor(value);
        //2.通过构造函数初始化类，并存入二级缓存
        Object o = InitClassByConstructor(constructor);
        //3.检查该Bean是否有AOP和Transaction注解，有的话要先产生代理类，并将最终代理类存入二级缓存 initializedClassCache

        //1.顺序问题，按自己想的来把Transaction放在最里层
        //2.如何解决事务的有责加入，没有则创建问题
        //这个就是针对Connection来处理的拿到连接设置自动提交为False就行了，另一种是新拿一个连接处理
        //本系统只支持一个线程一个连接
        // AOP-Transaction处理
        // 1.AOP支持用户注入（定义一个变量
        // 1.判断该类上是否有@Transactional注解（可以定义一个变量存储需要进行AOP处理的注解，以及这些注解需要AOP种的什么操作）
        // AOP就是使用目录匹配，先扫描AOP配置文件限定那些目录下的需要代理，然后循环创建类的时候判断该类是否在这个目录下，如果是则按照AOP的代理顺序进行代理即可
        HashMap<String, HashSet<String>> stringHashSetHashMap = new HashMap<>();
        AOPProcessing aopProcessing = new AOPProcessing(o);
        HashSet<String> hashSet = new HashSet<>();
        //ArrayList<String> arrayList = aopProcessing.GetTopClassContainTransactionAnnotation(o.getClass(), hashSet);

        beansInCreation.remove(key);
        initializedClassCache.put(key, value);
        loadClassCache.remove(key);
        //4.对初始化完成的类进行属性注入（注入过程中如果有依赖，将依赖注入完成后的属性类存入二级缓存）
        fieldWire.FieldAutowired(o);
        methodWired.MethodAutowired(o);
        beanCache.put(key, o);
        initializedClassCache.remove(key);

        //5.返回注入完成的类
        return o;
    }

    public Constructor GetInitConstructor(Class value) {
        Constructor constructor = null;
        try {
            constructor = value.getConstructor();
        } catch (NoSuchMethodException e) {
            try {
                ConstructorSet constructors = new ConstructorSet();
                for (Constructor<?> valueConstructor : value.getConstructors()) {
                    constructors.Add(valueConstructor, valueConstructor.getParameterCount(), valueConstructor.isAnnotationPresent(UseThisConstructor.class), value);
                }
                constructor = constructors.GetInitConstructor();
            } catch (Exception ex) {
                //没有可用的构造函数
                ex.printStackTrace();
            }
        }
        return constructor;
    }

    private Object InitClassByConstructor(Constructor constructor) throws Exception {
        Class declaringClass = constructor.getDeclaringClass();
        ArrayList<Object> objects = new ArrayList<>();
        Object o = null;
        Parameter[] parameters = constructor.getParameters();
        for (Parameter parameter : parameters) {
            //根据参数获取对应类型的值
            o = typeProcess.GetValueBasedOnClassTypeAndAnnotation(parameter.getType(), parameter.getAnnotation(Autowired.class), declaringClass.getName());
            objects.add(o);
        }
        Object instance = constructor.newInstance(objects.toArray());
        return instance;
    }

    //性能提升，将来可以研究将三个Object类型的map换成泛型
    //缓存原则：从哪放入从哪移除
    public Object GetBean(String key, String value, String referenceClassName) throws Exception {
        Object o;
        //初始化前先查找3，2，1级缓存中有没有
        if (!beanCache.containsKey(key)) {
            if (!initializedClassCache.containsKey(key)) {
                if (!loadClassCache.containsKey(key)) {
                    if (value == null) {
                        return null;
                    } else {
                        //如果一级缓存中没有，去加载它，然后放入一级缓存
                        loadClassCache.put(key, value);
                        //并初始化，放入二级缓存
                        //最后注入属性，放入三级缓存，同时清空一二级缓存
                        o = InitBean(key, loadClassCache.get(key));
                        //从一级缓存中移除，并存入二级缓存
                    }
                } else {
                    //一级缓存有
                    if (beansInCreation.contains(key)) {
                        throw new Exception(value + " 与 " + referenceClassName + " 循环依赖");
                    } else if (value == null) {
                        return null;
                    } else {
                        //开始创建对象，放入二级缓存，并属性注入，完成后放入三及缓存，同时清空二级缓存和一级缓存
                        o = InitBean(key, loadClassCache.get(key));
                        //return beanCache.get(key);-
                    }
                }
            } else {
                //二级缓存有，直接获取实例对象，解决循环依赖问题
                o = initializedClassCache.get(key);
            }
        } else {
            //三级缓存有，直接返回
            o = beanCache.get(key);
        }
        return o;
    }
}
