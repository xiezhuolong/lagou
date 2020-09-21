package com.lagou.edu.start.aop;

import com.lagou.edu.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class AOPProcessing {

    HashMap<String, HashSet<String>> hashMap;

    public HashMap<String, HashSet<String>> getHashMap() {
        return hashMap;
    }

    public AOPProcessing(HashMap<String, HashSet<String>> hashMap) {
        this.hashMap = hashMap;
    }

    public ArrayList<String> GetTopClassContainTransactionAnnotation(Class c, HashSet<String> bottomHashSet) {
        Object clone = bottomHashSet.clone();
        HashSet<String> hashSet = bottomHashSet;
        boolean b = bottomHashSet.addAll(bottomHashSet);
        hashSet.add(c.getName());
        if (c.getAnnotation(Transactional.class) != null) {
            if (hashMap.containsKey(c.getName())) {
                hashMap.get(c.getName());
            }
            hashMap.put(c.getName(), hashSet);
        }
        Class[] interfaces = c.getInterfaces();
        if (interfaces.length != 0) {
            for (Class anInterface : interfaces) {
                GetTopClassContainTransactionAnnotation(anInterface, hashSet);
            }
        }
        Class superclass = c.getSuperclass();
        if (superclass != null) {
            GetTopClassContainTransactionAnnotation(superclass, hashSet);
        }
        return null;
    }
}
