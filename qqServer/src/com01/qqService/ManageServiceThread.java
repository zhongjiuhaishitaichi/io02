package com01.qqService;

import java.util.HashMap;
import java.util.Iterator;


public class ManageServiceThread {
    private static HashMap<String, ServiceThread> hashMap = new HashMap<>();

    public static HashMap<String, ServiceThread> getHashMap() {
        return hashMap;
    }

    public static void add(String userId, ServiceThread serviceThread) {
        hashMap.put(userId, serviceThread);
    }

    public static ServiceThread get(String userId) {
        return hashMap.get(userId);
    }
    public static void remove(String userId){
        hashMap.remove(userId);
    }
    public static String getOnlineList() {
        Iterator<String> iterator = hashMap.keySet().iterator();
        String onlineList="";
        while (iterator.hasNext()) {
            onlineList+=iterator.next().toString()+" ";
        }
        return onlineList;
    }
}
