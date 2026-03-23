package com.ppp.rpc.loadbalance.impl;

import cn.hutool.core.util.RandomUtil;
import com.ppp.rpc.loadbalance.LoadBalance;

import java.util.List;

public class RandomLoadBalance implements LoadBalance {
    @Override
    public String select(List<String> list, String requestId) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        return RandomUtil.randomEle(list);
    }
}
