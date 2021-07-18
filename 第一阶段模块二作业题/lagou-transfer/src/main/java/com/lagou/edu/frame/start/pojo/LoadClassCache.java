package com.lagou.edu.frame.start.pojo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 一级缓存
 */
public class LoadClassCache {
    ClassLoader classLoader = this.getClass().getClassLoader();
    HashMap<String, Class> hashMap = new HashMap<>();

    public HashMap<String, Class> getCache() {
        return hashMap;
    }

    static LoadClassCache loadClassCache = new LoadClassCache();

    private LoadClassCache() {
    }

    public static LoadClassCache getInstance() {
        return loadClassCache;
    }

    public Object put(String className, String referenceName) throws ClassNotFoundException {
        Object put = hashMap.put(className, classLoader.loadClass(referenceName));
        return put;
    }

    public boolean containsKey(String key) {
        return hashMap.containsKey(key);
    }

    public Object remove(String key) {
        Object remove = hashMap.remove(key);
        return remove;
    }

    public Class get(Object key) {
        return hashMap.get(key);
    }

    public void putAll(HashMap<String, Class> m) {
        hashMap.putAll(m);
    }

    public int size() {
        return 0;
    }

    public boolean isEmpty() {
        return false;
    }

    public boolean containsValue(Object o) {
        return false;
    }

    public void clear() {
    }

    public Set<String> keySet() {
        return null;
    }

    public Collection<String> values() {
        return null;
    }

    public Set<Map.Entry<String, String>> entrySet() {
        return null;
    }
}
