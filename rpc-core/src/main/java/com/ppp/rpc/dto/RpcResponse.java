package com.ppp.rpc.dto;

import com.ppp.rpc.enums.RpcResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RpcResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private String requestId;
    private Integer code;
    private String msg;
    private T data;


    public static <T> RpcResponse<T> success(String requestId, T data) {
        RpcResponse<T> rpcResponse = new RpcResponse<T>();
        rpcResponse.setRequestId(requestId);
        rpcResponse.setCode(RpcResponseStatus.SUCCESS.getCode());
        rpcResponse.setData(data);

        return rpcResponse;
    }

    public static <T> RpcResponse<T> fail(String requestId, RpcResponseStatus status) {
        RpcResponse<T> rpcResponse = new RpcResponse<T>();
        rpcResponse.setRequestId(requestId);
        rpcResponse.setCode(status.getCode());
        rpcResponse.setMsg(status.getMsg());

        return rpcResponse;
    }

    public static <T> RpcResponse<T> fail(String requestId, String msg) {
        RpcResponse<T> rpcResponse = new RpcResponse<T>();
        rpcResponse.setRequestId(requestId);
        rpcResponse.setCode(RpcResponseStatus.FAIL.getCode());
        rpcResponse.setMsg(msg);

        return rpcResponse;
    }
}
