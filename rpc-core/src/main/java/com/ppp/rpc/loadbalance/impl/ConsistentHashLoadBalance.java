package com.ppp.rpc.loadbalance.impl;

import com.ppp.rpc.loadbalance.LoadBalance;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class ConsistentHashLoadBalance implements LoadBalance {
    private final TreeMap<Integer, String> virtualNodes = new TreeMap<>();
    private static final int VIRTUAL_NODES = 100;

    @Override
    public String select(List<String> list, String requestId) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        // 构建虚拟节点
        virtualNodes.clear();
        for (String address : list) {
            for (int i = 0; i < VIRTUAL_NODES; i++) {
                int hash = getHash(address + i);
                virtualNodes.put(hash, address);
            }
        }

        // 使用requestId作为哈希值，确保同一请求会路由到同一服务端
        int hash = getHash(requestId);
        SortedMap<Integer, String> tailMap = virtualNodes.tailMap(hash);
        String address;
        if (tailMap.isEmpty()) {
            if (!virtualNodes.isEmpty()) {
                address = virtualNodes.values().iterator().next();
            } else {
                return null;
            }
        } else {
            if (!tailMap.isEmpty()) {
                address = tailMap.values().iterator().next();
            } else {
                return null;
            }
        }

        return address;
    }

    private int getHash(String key) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < key.length(); i++) {
            hash = (hash ^ key.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }
}
