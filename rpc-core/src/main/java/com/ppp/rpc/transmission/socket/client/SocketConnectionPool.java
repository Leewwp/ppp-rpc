package com.ppp.rpc.transmission.socket.client;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SocketConnectionPool {
    private static final int POOL_SIZE = 16;
    private static final long CONNECTION_TIMEOUT = 30000;

    private final int poolSize;
    private final long connectionTimeout;
    private final java.util.concurrent.ConcurrentHashMap<InetSocketAddress, BlockingQueue<PooledSocketConnection>> connectionPools;

    public SocketConnectionPool() {
        this(POOL_SIZE, CONNECTION_TIMEOUT);
    }

    public SocketConnectionPool(int poolSize, long connectionTimeout) {
        this.poolSize = poolSize;
        this.connectionTimeout = connectionTimeout;
        this.connectionPools = new java.util.concurrent.ConcurrentHashMap<>();
    }

    public PooledSocketConnection getConnection(InetSocketAddress address) throws IOException {
        BlockingQueue<PooledSocketConnection> pool = connectionPools.computeIfAbsent(
            address,
            k -> new LinkedBlockingQueue<>(poolSize)
        );

        PooledSocketConnection connection;
        try {
            connection = pool.poll(connectionTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Connection pool interrupted while waiting for connection to: " + address, e);
        }
        if (connection == null) {
            throw new IOException("Connection pool exhausted, timeout waiting for connection to: " + address);
        }

        if (!connection.isConnected()) {
            try {
                connection.close();
            } catch (IOException e) {
                log.warn("Error closing dead connection: {}", e.getMessage());
            }
            connection = createNewConnection(address);
        }

        return connection;
    }

    public void returnConnection(InetSocketAddress address, PooledSocketConnection connection) {
        if (connection == null) {
            return;
        }

        BlockingQueue<PooledSocketConnection> pool = connectionPools.get(address);
        if (pool == null) {
            try {
                connection.close();
            } catch (IOException e) {
                log.warn("Error closing connection: {}", e.getMessage());
            }
            return;
        }

        if (pool.size() < poolSize && connection.isConnected()) {
            pool.offer(connection);
        } else {
            try {
                connection.close();
            } catch (IOException e) {
                log.warn("Error closing excess connection: {}", e.getMessage());
            }
        }
    }

    public void closePool(InetSocketAddress address) {
        BlockingQueue<PooledSocketConnection> pool = connectionPools.remove(address);
        if (pool != null) {
            for (PooledSocketConnection connection : pool) {
                try {
                    connection.close();
                } catch (IOException e) {
                    log.warn("Error closing connection: {}", e.getMessage());
                }
            }
        }
    }

    public void closeAll() {
        for (InetSocketAddress address : connectionPools.keySet()) {
            closePool(address);
        }
    }

    private PooledSocketConnection createNewConnection(InetSocketAddress address) throws IOException {
        Socket socket = new Socket(address.getAddress(), address.getPort());
        socket.setKeepAlive(true);
        socket.setTcpNoDelay(true);
        return new PooledSocketConnection(socket, address);
    }

    public static class PooledSocketConnection {
        private final Socket socket;
        private final InetSocketAddress address;

        public PooledSocketConnection(Socket socket, InetSocketAddress address) {
            this.socket = socket;
            this.address = address;
        }

        public Socket getSocket() {
            return socket;
        }

        public InetSocketAddress getAddress() {
            return address;
        }

        public boolean isConnected() {
            return socket != null && socket.isConnected() && !socket.isClosed();
        }

        public void close() throws IOException {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
