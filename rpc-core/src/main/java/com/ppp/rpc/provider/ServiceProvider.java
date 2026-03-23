package com.ppp.rpc.provider;

import com.ppp.rpc.config.RpcServiceConfig;

public interface ServiceProvider {
    void publishService(RpcServiceConfig config);

    Object getService(String rpcServiceName);
}
