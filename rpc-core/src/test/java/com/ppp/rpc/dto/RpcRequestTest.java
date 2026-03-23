package com.ppp.rpc.dto;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class RpcRequestTest {

    @Test
    void rpcServiceNameShouldCombineInterfaceNameWithVersionAndGroup() {
        RpcRequest request = RpcRequest.builder()
            .requestId("test-id")
            .interfaceName("com.example.UserService")
            .methodName("getUser")
            .version("1.0")
            .group("default")
            .params(new Object[]{1L})
            .paramTypes(new Class[]{Long.class})
            .build();

        assertEquals("com.example.UserService1.0default", request.rpcServiceName());
    }

    @Test
    void rpcServiceNameShouldHandleNullVersionAndGroup() {
        RpcRequest request = RpcRequest.builder()
            .requestId("test-id")
            .interfaceName("com.example.UserService")
            .methodName("getUser")
            .version(null)
            .group(null)
            .params(new Object[]{1L})
            .paramTypes(new Class[]{Long.class})
            .build();

        assertEquals("com.example.UserService", request.rpcServiceName());
    }

    @Test
    void rpcServiceNameShouldHandleEmptyVersionAndGroup() {
        RpcRequest request = RpcRequest.builder()
            .requestId("test-id")
            .interfaceName("com.example.UserService")
            .methodName("getUser")
            .version("")
            .group("")
            .params(new Object[]{1L})
            .paramTypes(new Class[]{Long.class})
            .build();

        assertEquals("com.example.UserService", request.rpcServiceName());
    }

    @Test
    void builderShouldCreateRequestWithAllFields() {
        RpcRequest request = RpcRequest.builder()
            .requestId("test-id")
            .interfaceName("com.example.UserService")
            .methodName("getUser")
            .params(new Object[]{1L, "test"})
            .paramTypes(new Class[]{Long.class, String.class})
            .version("1.0")
            .group("vip")
            .build();

        assertEquals("test-id", request.getRequestId());
        assertEquals("com.example.UserService", request.getInterfaceName());
        assertEquals("getUser", request.getMethodName());
        assertEquals(2, request.getParams().length);
        assertEquals(2, request.getParamTypes().length);
        assertEquals("1.0", request.getVersion());
        assertEquals("vip", request.getGroup());
    }

    @Test
    void defaultRequestShouldHaveNoArgs() {
        RpcRequest request = new RpcRequest();
        assertNull(request.getRequestId());
        assertNull(request.getInterfaceName());
        assertNull(request.getMethodName());
    }
}
