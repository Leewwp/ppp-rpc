package com.ppp.rpc.handler;

import com.ppp.rpc.dto.RpcRequest;
import com.ppp.rpc.exception.RpcException;
import com.ppp.rpc.monitor.RpcMonitor;
import com.ppp.rpc.provider.ServiceProvider;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

@Slf4j
public class RpcRequestHandler {
    private final ServiceProvider serviceProvider;

    public RpcRequestHandler(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public Object invoke(RpcRequest rpcRequest) {
        String methodName = rpcRequest.getInterfaceName() + "." + rpcRequest.getMethodName();
        RpcMonitor monitor = RpcMonitor.getInstance();
        long startTime = System.currentTimeMillis();

        monitor.recordMethodCall(methodName);

        String rpcServiceName = rpcRequest.rpcServiceName();
        Object service;
        try {
            service = serviceProvider.getService(rpcServiceName);
            if (service == null) {
                throw new RpcException.ServiceNotFoundException("Service not found: " + rpcServiceName);
            }

            log.debug("获取到对应服务: {}", service.getClass().getCanonicalName());

            Method method;
            try {
                method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            } catch (NoSuchMethodException e) {
                throw new RpcException.MethodNotFoundException("Method not found: " + rpcRequest.getMethodName(), e);
            }

            try {
                Object result = method.invoke(service, rpcRequest.getParams());
                long elapsedTime = System.currentTimeMillis() - startTime;
                monitor.recordMethodTime(methodName, elapsedTime);
                log.debug("方法调用成功，耗时: {}ms", elapsedTime);
                return result;
            } catch (Exception e) {
                monitor.recordError(methodName);
                throw new RpcException("Method invocation failed: " + e.getMessage(), e);
            }
        } catch (RpcException e) {
            monitor.recordError(methodName);
            throw e;
        } catch (Exception e) {
            monitor.recordError(methodName);
            throw new RpcException("Service invocation failed: " + e.getMessage(), e);
        }
    }
}
