package com.ppp.rpc.dto;

import com.ppp.rpc.enums.RpcResponseStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RpcResponseTest {

    @Test
    void successShouldCreateSuccessfulResponse() {
        RpcResponse<String> response = RpcResponse.success("request-123", "result-data");

        assertEquals("request-123", response.getRequestId());
        assertEquals(RpcResponseStatus.SUCCESS.getCode(), response.getCode());
        assertEquals("result-data", response.getData());
        assertNull(response.getMsg());
    }

    @Test
    void successShouldHandleNullData() {
        RpcResponse<Void> response = RpcResponse.success("request-123", null);

        assertEquals("request-123", response.getRequestId());
        assertEquals(RpcResponseStatus.SUCCESS.getCode(), response.getCode());
        assertNull(response.getData());
    }

    @Test
    void failShouldCreateFailedResponse() {
        RpcResponse<Void> response = RpcResponse.fail("request-123", "Service not found");

        assertEquals("request-123", response.getRequestId());
        assertEquals(RpcResponseStatus.FAIL.getCode(), response.getCode());
        assertNull(response.getData());
        assertEquals("Service not found", response.getMsg());
    }

    @Test
    void isFailedShouldReturnTrueForFailureStatus() {
        RpcResponse<Void> response = RpcResponse.fail("request-123", "Error");
        assertTrue(RpcResponseStatus.isFailed(response.getCode()));
    }

    @Test
    void isFailedShouldReturnFalseForSuccessStatus() {
        RpcResponse<String> response = RpcResponse.success("request-123", "OK");
        assertFalse(RpcResponseStatus.isFailed(response.getCode()));
    }
}
