package com.ppp.rpc.loadbalance.impl;

import com.ppp.rpc.loadbalance.LoadBalance;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RandomLoadBalanceTest {

    private final LoadBalance loadBalance = new RandomLoadBalance();

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
    void selectShouldReturnOneOfTheAddressesWhenListHasMultipleItems() {
        List<String> list = Arrays.asList(
            "192.168.1.1:8888",
            "192.168.1.2:8888",
            "192.168.1.3:8888"
        );

        // Run multiple times to verify randomness
        boolean found1 = false, found2 = false, found3 = false;
        for (int i = 0; i < 100; i++) {
            String result = loadBalance.select(list, "test-request");
            if (result.equals("192.168.1.1:8888")) found1 = true;
            if (result.equals("192.168.1.2:8888")) found2 = true;
            if (result.equals("192.168.1.3:8888")) found3 = true;
        }

        // Should find all addresses with enough iterations (statistically very likely)
        assertTrue(found1, "Should select first address at least once");
        assertTrue(found2, "Should select second address at least once");
        assertTrue(found3, "Should select third address at least once");
    }
}
