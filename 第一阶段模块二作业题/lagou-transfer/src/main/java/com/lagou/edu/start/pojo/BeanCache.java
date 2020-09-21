package com.lagou.edu.start.pojo;

import java.util.concurrent.ConcurrentHashMap;

public class BeanCache {

    static ConcurrentHashMap<String, Object> beanCache = new ConcurrentHashMap<>();

    public static ConcurrentHashMap<String, Object> getBeanCache() {
        return beanCache;
    }
}
