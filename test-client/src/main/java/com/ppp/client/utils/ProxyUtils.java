package com.ppp.client.utils;

import com.ppp.rpc.factory.SingletonFactory;
import com.ppp.rpc.proxy.RpcClientProxy;
import com.ppp.rpc.transmission.RpcClient;
import com.ppp.rpc.transmission.socket.client.SocketRpcClient;

public class ProxyUtils {
    private static final RpcClient rpcClient = SingletonFactory.getInstance(SocketRpcClient.class);
    private static final RpcClientProxy rpcClientProxy = new RpcClientProxy(rpcClient);

    public static <T> T getProxy(Class<T> clazz) {
        return rpcClientProxy.getProxy(clazz);
    }
}
