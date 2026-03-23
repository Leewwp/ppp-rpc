package com.ppp.rpc.transmission;

import com.ppp.rpc.config.RpcServiceConfig;

public interface RpcServer {
    void start();

    void publishService(RpcServiceConfig config);
}
