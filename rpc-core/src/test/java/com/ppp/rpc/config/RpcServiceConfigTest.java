package com.ppp.rpc.config;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RpcServiceConfigTest {

    @Test
    void defaultConfigShouldHaveEmptyVersionAndGroup() {
        RpcServiceConfig config = new RpcServiceConfig();
        assertEquals("", config.getVersion());
        assertEquals("", config.getGroup());
        assertNull(config.getService());
    }

    @Test
    void rpcServiceNamesShouldGenerateCorrectServiceNames() {
        TestService service = new TestService();
        RpcServiceConfig config = new RpcServiceConfig("1.0", "group1", service);

        List<String> serviceNames = config.rpcServiceNames();
        assertEquals(1, serviceNames.size());
        // RpcServiceConfig uses getClass().getInterfaces() which returns Runnable, not TestService
        assertEquals(Runnable.class.getCanonicalName() + "1.0group1", serviceNames.get(0));
    }

    @Test
    void rpcServiceNamesShouldHandleMultipleInterfaces() {
        TestServiceWithTwoInterfaces service = new TestServiceWithTwoInterfaces();
        RpcServiceConfig config = new RpcServiceConfig("2.0", "default", service);

        List<String> serviceNames = config.rpcServiceNames();
        assertEquals(2, serviceNames.size());
        // Should contain both Runnable and AutoCloseable
        assertTrue(serviceNames.contains(Runnable.class.getCanonicalName() + "2.0default"));
        assertTrue(serviceNames.contains(AutoCloseable.class.getCanonicalName() + "2.0default"));
    }

    // Test helper classes
    public static class TestService implements Runnable {
        @Override
        public void run() {}
    }

    public static class TestServiceWithTwoInterfaces implements Runnable, AutoCloseable {
        @Override
        public void run() {}
        @Override
        public void close() {}
    }
}
