package com.ppp.rpc.loadbalance.impl;

import com.ppp.rpc.loadbalance.LoadBalance;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRobinLoadBalance implements LoadBalance {
    private final AtomicInteger index = new AtomicInteger(0);

    @Override
    public String select(List<String> list, String requestId) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        int size = list.size();
        // 取模运算，避免线程越界
        int i = index.getAndIncrement() % size;
        // 处理负数情况
        if (i < 0) {
            i += size;
        }
        return list.get(i);
    }
}
