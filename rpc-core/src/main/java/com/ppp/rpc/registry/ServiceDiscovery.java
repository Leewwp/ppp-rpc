package com.ppp.rpc.registry;

import com.ppp.rpc.dto.RpcRequest;

import java.net.InetSocketAddress;

public interface ServiceDiscovery {
    InetSocketAddress lookupService(RpcRequest rpcRequest);
}
