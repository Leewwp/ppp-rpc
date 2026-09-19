package com.ppp.rpc.transmission.socket.server;

import com.ppp.rpc.config.RpcServiceConfig;
import com.ppp.rpc.constant.RpcConstant;
import com.ppp.rpc.factory.SingletonFactory;
import com.ppp.rpc.handler.RpcRequestHandler;
import com.ppp.rpc.monitor.RpcMonitor;
import com.ppp.rpc.provider.ServiceProvider;
import com.ppp.rpc.provider.impl.ZkServiceProvider;
import com.ppp.rpc.transmission.RpcServer;
import com.ppp.rpc.util.ShutdownHookUtils;
import com.ppp.rpc.util.ThreadPoolUtils;
import lombok.extern.slf4j.Slf4j;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

@Slf4j
public class SocketRpcServer implements RpcServer {
    private final int port;
    private final RpcRequestHandler rpcRequestHandler;
    private final ServiceProvider serviceProvider;
    private final ExecutorService executor;


    public SocketRpcServer() {
        this(RpcConstant.SERVER_PORT);
    }

    public SocketRpcServer(int port) {
        this(port, SingletonFactory.getInstance(ZkServiceProvider.class));
    }

    public SocketRpcServer(int port, ServiceProvider serviceProvider) {
        this.port = port;
        this.serviceProvider = serviceProvider;
        this.rpcRequestHandler = new RpcRequestHandler(serviceProvider);
        this.executor = ThreadPoolUtils.createIoIntensiveThreadPool("socket-rpc-server-");
    }

    @Override
    public void start() {
        ShutdownHookUtils.clearAll();
        ShutdownHookUtils.addShutdownHook(() -> {
            log.info("服务关闭，打印监控信息..");
            RpcMonitor.getInstance().printMetrics();
        });

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("服务启动, 端口: {}", port);

            Socket socket;
            while ((socket = serverSocket.accept()) != null) {
                executor.submit(new SocketReqHandler(socket, rpcRequestHandler));
            }
        } catch (Exception e) {
            log.error("服务端异常", e);
        }
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        serviceProvider.publishService(config);
    }
}
