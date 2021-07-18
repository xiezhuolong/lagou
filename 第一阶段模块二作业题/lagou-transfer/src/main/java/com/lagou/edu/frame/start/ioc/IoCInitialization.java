package com.lagou.edu.frame.start.ioc;

import com.lagou.edu.frame.start.pojo.LoadClassCache;

import java.util.HashMap;

public class IoCInitialization {

    //使用建造者模式主要是为了给 ioC_bean_range 属性赋默认值
    //实现是否设置初值都支持的情况
    private String ioC_bean_range = "";

    public IoCInitialization setIoC_bean_range(String ioC_bean_range) {
        this.ioC_bean_range = ioC_bean_range;
        return this;
    }

    //1.需要通过读取的hashmap初始化里面的所有类
    public void InitIoC() throws Exception {
        //扫描需要让容器管理的Java类，并放入一级缓存
        HashMap<String, Class> originLoadClass = new JavaFileLoadBuilder().ScannerAllJavaFile(ioC_bean_range);
        LoadClassCache.getInstance().putAll(originLoadClass);
        //将Java类初始化，并注入属性
        new BeanFactory().InitOriginLoadClassBean(originLoadClass);
    }
}
