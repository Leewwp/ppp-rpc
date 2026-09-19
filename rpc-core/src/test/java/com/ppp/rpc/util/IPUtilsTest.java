package com.ppp.rpc.util;

import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;

import static org.junit.jupiter.api.Assertions.*;

class IPUtilsTest {

    @Test
    void toIpPortShouldConvertAddressToString() {
        InetSocketAddress address = new InetSocketAddress("192.168.1.1", 8888);
        String result = IPUtils.toIpPort(address);
        assertEquals("192.168.1.1:8888", result);
    }

    @Test
    void toIpPortShouldConvertLocalhostTo127001() {
        InetSocketAddress address = new InetSocketAddress("localhost", 8888);
        String result = IPUtils.toIpPort(address);
        assertEquals("127.0.0.1:8888", result);
    }

    @Test
    void toIpPortShouldThrowExceptionForNullAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            IPUtils.toIpPort(null);
        });
    }

    @Test
    void toInetSocketAddressShouldConvertStringToAddress() {
        InetSocketAddress result = IPUtils.toInetSocketAddress("192.168.1.1:8888");
        assertEquals("192.168.1.1", result.getHostString());
        assertEquals(8888, result.getPort());
    }

    @Test
    void toInetSocketAddressShouldThrowExceptionForBlankAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            IPUtils.toInetSocketAddress("");
        });
        assertThrows(IllegalArgumentException.class, () -> {
            IPUtils.toInetSocketAddress(null);
        });
    }

    @Test
    void toInetSocketAddressShouldThrowExceptionForInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> {
            IPUtils.toInetSocketAddress("192.168.1.1"); // Missing port
        });
        assertThrows(IllegalArgumentException.class, () -> {
            IPUtils.toInetSocketAddress("192.168.1.1:abc"); // Invalid port
        });
    }
}
