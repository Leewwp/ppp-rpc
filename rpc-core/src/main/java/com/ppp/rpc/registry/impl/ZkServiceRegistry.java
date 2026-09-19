package com.ppp.rpc.registry.impl;

import cn.hutool.core.util.StrUtil;
import com.ppp.rpc.constant.RpcConstant;
import com.ppp.rpc.factory.SingletonFactory;
import com.ppp.rpc.registry.ServiceRegistry;
import com.ppp.rpc.registry.zk.ZkClient;
import com.ppp.rpc.util.IPUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;

@Slf4j
public class ZkServiceRegistry implements ServiceRegistry {
    private final ZkClient zkClient;

    public ZkServiceRegistry() {
        this(SingletonFactory.getInstance(ZkClient.class));
    }

    public ZkServiceRegistry(ZkClient zkClient) {
        this.zkClient = zkClient;
    }

    @Override
    public void registerService(String rpcServiceName, InetSocketAddress address) {
        log.info("服务注册, rpcServiceName: {}, address: {}", rpcServiceName, address);

        String path = RpcConstant.ZK_RPC_ROOT_PATH
            + StrUtil.SLASH
            + rpcServiceName
            + StrUtil.SLASH
            + IPUtils.toIpPort(address);

        zkClient.createPersistentNode(path);
    }

    @SneakyThrows
    @Override
    public void clearAll() {
        String host = InetAddress.getLocalHost().getHostAddress();
        int port = RpcConstant.SERVER_PORT;
        zkClient.clearAll(new InetSocketAddress(host, port));
    }
}
