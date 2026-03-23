package com.ppp.rpc.proxy;

import cn.hutool.core.util.IdUtil;
import com.ppp.rpc.config.RpcServiceConfig;
import com.ppp.rpc.dto.RpcRequest;
import com.ppp.rpc.dto.RpcResponse;
import com.ppp.rpc.enums.RpcResponseStatus;
import com.ppp.rpc.exception.RpcException;
import com.ppp.rpc.transmission.RpcClient;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

public class RpcClientProxy implements InvocationHandler {
    private final RpcClient rpcClient;
    private final RpcServiceConfig config;

    public RpcClientProxy(RpcClient rpcClient) {
        this(rpcClient, new RpcServiceConfig());
    }

    public RpcClientProxy(RpcClient rpcClient, RpcServiceConfig config) {
        this.rpcClient = rpcClient;
        this.config = config;
    }


    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(
            clazz.getClassLoader(),
            new Class[]{clazz},
            this
        );
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest rpcRequest = RpcRequest.builder()
            .requestId(IdUtil.fastSimpleUUID())
            .interfaceName(method.getDeclaringClass().getCanonicalName())
            .methodName(method.getName())
            .params(args)
            .paramTypes(method.getParameterTypes())
            .version(config.getVersion())
            .group(config.getGroup())
            .build();

        RpcResponse<?> rpcResponse = rpcClient.sendReq(rpcRequest);

        check(rpcRequest, rpcResponse);

        return rpcResponse.getData();
    }

    private void check(RpcRequest rpcRequest, RpcResponse<?> rpcResponse) {
        if (Objects.isNull(rpcResponse)) {
            throw new RpcException("rpcResp为空");
        }

        if (!Objects.equals(rpcRequest.getRequestId(), rpcResponse.getRequestId())) {
            throw new RpcException("请求和响应的id不一致");
        }

        if (RpcResponseStatus.isFailed(rpcResponse.getCode())) {
            throw new RpcException("响应值为失败: " + rpcResponse.getMsg());
        }
    }
}
