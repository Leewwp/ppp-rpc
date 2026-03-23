package com.ppp.rpc.registry.impl;

import cn.hutool.core.util.StrUtil;
import com.ppp.rpc.constant.RpcConstant;
import com.ppp.rpc.dto.RpcRequest;
import com.ppp.rpc.factory.SingletonFactory;
import com.ppp.rpc.loadbalance.LoadBalance;
import com.ppp.rpc.loadbalance.LoadBalanceFactory;
import com.ppp.rpc.registry.ServiceDiscovery;
import com.ppp.rpc.registry.zk.ZkClient;
import com.ppp.rpc.util.IPUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class ZkServiceDiscovery implements ServiceDiscovery {
    private final ZkClient zkClient;
    private final LoadBalance loadBalance;

    public ZkServiceDiscovery() {
        this(
            SingletonFactory.getInstance(ZkClient.class),
            LoadBalanceFactory.getLoadBalance("random")
        );
    }

    public ZkServiceDiscovery(ZkClient zkClient, LoadBalance loadBalance) {
        this.zkClient = zkClient;
        this.loadBalance = loadBalance;
    }

    public ZkServiceDiscovery(String loadBalanceType) {
        this(
            SingletonFactory.getInstance(ZkClient.class),
            LoadBalanceFactory.getLoadBalance(loadBalanceType)
        );
    }

    @Override
    public InetSocketAddress lookupService(RpcRequest rpcRequest) {
        String path = RpcConstant.ZK_RPC_ROOT_PATH
            + StrUtil.SLASH
            + rpcRequest.rpcServiceName();

        List<String> children = zkClient.getChildrenNode(path);
        if (children == null || children.isEmpty()) {
            log.error("No service found for: {}", rpcRequest.rpcServiceName());
            return null;
        }
        
        String address = loadBalance.select(children, rpcRequest.getRequestId());
        log.debug("Selected service address: {}", address);

        return IPUtils.toInetSocketAddress(address);
    }
}
