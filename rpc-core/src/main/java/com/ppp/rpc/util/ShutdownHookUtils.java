package com.ppp.rpc.util;

import com.ppp.rpc.factory.SingletonFactory;
import com.ppp.rpc.registry.ServiceRegistry;
import com.ppp.rpc.registry.impl.ZkServiceRegistry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShutdownHookUtils {
    public static void clearAll() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("系统结束运行, 清理资源");
            ServiceRegistry serviceRegistry = SingletonFactory.getInstance(ZkServiceRegistry.class);
            serviceRegistry.clearAll();
            ThreadPoolUtils.shutdownAll();
        }));
    }

    public static void addShutdownHook(Runnable hook) {
        Runtime.getRuntime().addShutdownHook(new Thread(hook));
    }
}
