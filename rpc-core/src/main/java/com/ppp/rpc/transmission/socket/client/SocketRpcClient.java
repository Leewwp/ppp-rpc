package com.ppp.rpc.transmission.socket.client;

import com.ppp.rpc.dto.RpcRequest;
import com.ppp.rpc.dto.RpcResponse;
import com.ppp.rpc.exception.RpcException;
import com.ppp.rpc.factory.SingletonFactory;
import com.ppp.rpc.registry.ServiceDiscovery;
import com.ppp.rpc.registry.impl.ZkServiceDiscovery;
import com.ppp.rpc.transmission.RpcClient;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

@Slf4j
public class SocketRpcClient implements RpcClient {
    private final ServiceDiscovery serviceDiscovery;
    private final SocketConnectionPool connectionPool;

    public SocketRpcClient() {
        this(SingletonFactory.getInstance(ZkServiceDiscovery.class));
    }

    public SocketRpcClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
        this.connectionPool = new SocketConnectionPool();
    }

    public SocketRpcClient(ServiceDiscovery serviceDiscovery, SocketConnectionPool connectionPool) {
        this.serviceDiscovery = serviceDiscovery;
        this.connectionPool = connectionPool;
    }

    @Override
    public RpcResponse<?> sendReq(RpcRequest rpcRequest) {
        InetSocketAddress address;
        try {
            address = serviceDiscovery.lookupService(rpcRequest);
            if (address == null) {
                throw new RpcException.RegistryException("Service address not found for: " + rpcRequest.rpcServiceName());
            }
        } catch (Exception e) {
            throw new RpcException.RegistryException("Service discovery failed: " + e.getMessage(), e);
        }

        log.debug("Found service address: {}", address);

        SocketConnectionPool.PooledSocketConnection pooledConnection = null;
        try {
            pooledConnection = connectionPool.getConnection(address);
            Socket socket = pooledConnection.getSocket();

            try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
                 ObjectOutputStream outputStream = new ObjectOutputStream(bufferedOutputStream);
                 BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
                 ObjectInputStream inputStream = new ObjectInputStream(bufferedInputStream)) {

                outputStream.writeObject(rpcRequest);
                outputStream.flush();
                bufferedOutputStream.flush();
                log.debug("RPC request sent: {}", rpcRequest);

                Object o = inputStream.readObject();
                log.debug("Received RPC response");
                return (RpcResponse<?>) o;
            }
        } catch (RpcException e) {
            throw e;
        } catch (Exception e) {
            if (pooledConnection != null) {
                try {
                    pooledConnection.close();
                } catch (Exception closeEx) {
                    log.warn("Error closing dead connection: {}", closeEx.getMessage());
                }
            }
            throw new RpcException.NetworkException("Network error: " + e.getMessage(), e);
        } finally {
            if (pooledConnection != null) {
                connectionPool.returnConnection(address, pooledConnection);
            }
        }
    }

    public void close() {
        connectionPool.closeAll();
    }
}
