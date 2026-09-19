package com.ppp.rpc.loadbalance;

import com.ppp.rpc.loadbalance.impl.ConsistentHashLoadBalance;
import com.ppp.rpc.loadbalance.impl.RandomLoadBalance;
import com.ppp.rpc.loadbalance.impl.RoundRobinLoadBalance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoadBalanceFactoryTest {

    @Test
    void getLoadBalanceShouldReturnRandomLoadBalanceForRandomType() {
        LoadBalance lb = LoadBalanceFactory.getLoadBalance("random");
        assertTrue(lb instanceof RandomLoadBalance);
    }

    @Test
    void getLoadBalanceShouldReturnRoundRobinLoadBalanceForRoundRobinType() {
        LoadBalance lb = LoadBalanceFactory.getLoadBalance("roundRobin");
        assertTrue(lb instanceof RoundRobinLoadBalance);
    }

    @Test
    void getLoadBalanceShouldReturnConsistentHashLoadBalanceForConsistentHashType() {
        LoadBalance lb = LoadBalanceFactory.getLoadBalance("consistentHash");
        assertTrue(lb instanceof ConsistentHashLoadBalance);
    }

    @Test
    void getLoadBalanceShouldReturnRandomLoadBalanceForUnknownType() {
        LoadBalance lb = LoadBalanceFactory.getLoadBalance("unknown");
        assertTrue(lb instanceof RandomLoadBalance, "Unknown type should default to RandomLoadBalance");
    }

    @Test
    void getLoadBalanceShouldReturnRandomLoadBalanceForNullType() {
        LoadBalance lb = LoadBalanceFactory.getLoadBalance(null);
        assertTrue(lb instanceof RandomLoadBalance);
    }
}
