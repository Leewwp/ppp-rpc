package com.ppp.rpc.loadbalance;

import com.ppp.rpc.loadbalance.impl.ConsistentHashLoadBalance;
import com.ppp.rpc.loadbalance.impl.RandomLoadBalance;
import com.ppp.rpc.loadbalance.impl.RoundRobinLoadBalance;

public class LoadBalanceFactory {
    public static LoadBalance getLoadBalance(String type) {
        if (type == null) {
            return new RandomLoadBalance();
        }
        switch (type) {
            case "random":
                return new RandomLoadBalance();
            case "roundRobin":
                return new RoundRobinLoadBalance();
            case "consistentHash":
                return new ConsistentHashLoadBalance();
            default:
                return new RandomLoadBalance();
        }
    }
}
