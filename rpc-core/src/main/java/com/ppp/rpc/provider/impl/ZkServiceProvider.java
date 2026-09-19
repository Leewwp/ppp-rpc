package com.ppp.rpc.provider.impl;

import cn.hutool.core.util.StrUtil;
import com.ppp.rpc.config.RpcServiceConfig;
import com.ppp.rpc.constant.RpcConstant;
import com.ppp.rpc.factory.SingletonFactory;
import com.ppp.rpc.provider.ServiceProvider;
import com.ppp.rpc.registry.ServiceRegistry;
import com.ppp.rpc.registry.impl.ZkServiceRegistry;
import lombok.SneakyThrows;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ZkServiceProvider implements ServiceProvider {
    private final Map<String, Object> SERVICE_CACHE = new HashMap<>();
    private final ServiceRegistry serviceRegistry;

    public ZkServiceProvider() {
        this(SingletonFactory.getInstance(ZkServiceRegistry.class));
    }

    public ZkServiceProvider(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        config.rpcServiceNames()
            .forEach(rpcServiceName -> publishService(rpcServiceName, config.getService()));
    }

    @Override
    public Object getService(String rpcServiceName) {
        if (StrUtil.isBlank(rpcServiceName)) {
            throw new IllegalArgumentException("rpcServiceName为空");
        }

        if (!SERVICE_CACHE.containsKey(rpcServiceName)) {
            throw new IllegalArgumentException("rpcServiceName未注册: " + rpcServiceName);
        }

        return SERVICE_CACHE.get(rpcServiceName);
    }

    @SneakyThrows
    private void publishService(String rpcServiceName, Object service) {
        String host = InetAddress.getLocalHost().getHostAddress();
        int port = RpcConstant.SERVER_PORT;

        InetSocketAddress address = new InetSocketAddress(host, port);
        serviceRegistry.registerService(rpcServiceName, address);

        SERVICE_CACHE.put(rpcServiceName, service);
    }
}
