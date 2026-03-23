package com.ppp.rpc.transmission.netty.client;

import com.ppp.rpc.dto.RpcRequest;
import com.ppp.rpc.dto.RpcResponse;
import com.ppp.rpc.enums.RpcResponseStatus;
import com.ppp.rpc.exception.RpcException;
import com.ppp.rpc.factory.SingletonFactory;
import com.ppp.rpc.registry.ServiceDiscovery;
import com.ppp.rpc.registry.impl.ZkServiceDiscovery;
import com.ppp.rpc.transmission.RpcClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class NettyRpcClient implements RpcClient {
    private final ServiceDiscovery serviceDiscovery;
    private final ConcurrentHashMap<String, RpcResponse<?>> responseMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CountDownLatch> latchMap = new ConcurrentHashMap<>();
    private Channel channel;

    public NettyRpcClient() {
        this(SingletonFactory.getInstance(ZkServiceDiscovery.class));
    }

    public NettyRpcClient(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
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

        log.debug("找到服务地址: {}", address);

        CountDownLatch latch = new CountDownLatch(1);
        String requestId = rpcRequest.getRequestId();
        latchMap.put(requestId, latch);

        try {
            doConnect(address, rpcRequest);
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RpcException.NetworkException("Request interrupted: " + e.getMessage(), e);
        }

        RpcResponse<?> response = responseMap.remove(requestId);
        latchMap.remove(requestId);

        if (response == null) {
            throw new RpcException.NetworkException("No response received for request: " + requestId);
        }

        return response;
    }

    private void doConnect(InetSocketAddress address, RpcRequest rpcRequest) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ObjectEncoder());
                        pipeline.addLast(new ObjectDecoder(
                            ClassResolvers.cacheDisabled(null)));
                        pipeline.addLast(new NettyClientHandler(responseMap, latchMap));
                    }
                });

            ChannelFuture future = bootstrap.connect(address.getAddress(), address.getPort()).sync();
            channel = future.channel();

            channel.writeAndFlush(rpcRequest).sync();
            log.debug("RPC请求发送成功: {}", rpcRequest);
        } catch (Exception e) {
            if (channel != null) {
                channel.close();
            }
            throw new RpcException.NetworkException("Failed to connect to server: " + e.getMessage(), e);
        }
    }

    public void close() {
        if (channel != null) {
            channel.close();
        }
    }
}
