package com.ppp.rpc.transmission;

import com.ppp.rpc.dto.RpcRequest;
import com.ppp.rpc.dto.RpcResponse;

public interface RpcClient {
    RpcResponse<?> sendReq(RpcRequest request);
}
