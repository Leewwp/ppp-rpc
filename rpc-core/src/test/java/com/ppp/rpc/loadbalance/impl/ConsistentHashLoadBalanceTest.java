package com.ppp.rpc.loadbalance.impl;

import com.ppp.rpc.loadbalance.LoadBalance;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConsistentHashLoadBalanceTest {

    private final LoadBalance loadBalance = new ConsistentHashLoadBalance();

    @Test
    void selectShouldReturnNullWhenListIsNull() {
        String result = loadBalance.select(null, "test-request");
        assertNull(result);
    }

    @Test
    void selectShouldReturnNullWhenListIsEmpty() {
        String result = loadBalance.select(Collections.emptyList(), "test-request");
        assertNull(result);
    }

    @Test
    void selectShouldReturnSingleElementWhenListHasOneItem() {
        List<String> list = Collections.singletonList("192.168.1.1:8888");
        String result = loadBalance.select(list, "test-request");
        assertEquals("192.168.1.1:8888", result);
    }

    @Test
    void selectShouldReturnConsistentAddressForSameRequestId() {
        List<String> list = Arrays.asList(
            "192.168.1.1:8888",
            "192.168.1.2:8888",
            "192.168.1.3:8888"
        );

        // Multiple calls with same requestId should return the same address
        String firstResult = loadBalance.select(list, "same-request");
        for (int i = 0; i < 10; i++) {
            assertEquals(firstResult, loadBalance.select(list, "same-request"),
                "Same requestId should get consistent result");
        }
    }
}
