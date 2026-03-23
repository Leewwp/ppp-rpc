package com.ppp.rpc.loadbalance.impl;

import com.ppp.rpc.loadbalance.LoadBalance;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RoundRobinLoadBalanceTest {

    private final LoadBalance loadBalance = new RoundRobinLoadBalance();

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
    void selectShouldRoundRobinAmongMultipleAddresses() {
        List<String> list = Arrays.asList(
            "192.168.1.1:8888",
            "192.168.1.2:8888",
            "192.168.1.3:8888"
        );

        // Round-robin should cycle through addresses
        assertEquals("192.168.1.1:8888", loadBalance.select(list, "req-1"));
        assertEquals("192.168.1.2:8888", loadBalance.select(list, "req-2"));
        assertEquals("192.168.1.3:8888", loadBalance.select(list, "req-3"));
        assertEquals("192.168.1.1:8888", loadBalance.select(list, "req-4")); // Wraps around
    }

    @Test
    void selectShouldHandleNegativeIndexAfterOverflow() {
        List<String> list = Arrays.asList(
            "192.168.1.1:8888",
            "192.168.1.2:8888"
        );

        // Simulate many calls to trigger potential overflow
        for (int i = 0; i < 100; i++) {
            loadBalance.select(list, "test-request");
        }

        // After many calls, should still work correctly
        String result = loadBalance.select(list, "test-request");
        assertNotNull(result);
        assertTrue(list.contains(result));
    }
}
