package com.lagou.edu.start.pojo;

import java.util.HashMap;

public class InitializedClassCache {
    HashMap<String, Object> hashMap = new HashMap<>();

    static InitializedClassCache initializedClassCache = new InitializedClassCache();

    private InitializedClassCache() {
    }

    public static InitializedClassCache getInstance() {
        return initializedClassCache;
    }

    public boolean containsKey(String key) {
        return hashMap.containsKey(key);
    }

    public Object remove(String key) {
        Object remove = hashMap.remove(key);
        return remove;
    }

    public Object put(String key, Object value) {
        return hashMap.put(key, value);
    }

    public Object get(Object key) {
        return hashMap.get(key);
    }
}
