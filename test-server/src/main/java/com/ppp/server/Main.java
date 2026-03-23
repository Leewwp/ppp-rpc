package com.ppp.server;

import com.ppp.rpc.config.RpcServiceConfig;
import com.ppp.rpc.transmission.RpcServer;
import com.ppp.rpc.transmission.socket.server.SocketRpcServer;
import com.ppp.server.service.UserServiceImpl;

public class Main {
    public static void main(String[] args) {
        RpcServiceConfig config = new RpcServiceConfig(new UserServiceImpl());

        RpcServer rpcServer = new SocketRpcServer();
        rpcServer.publishService(config);

        rpcServer.start();

    }
}
