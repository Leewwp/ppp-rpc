package com.ppp.rpc.transmission.netty.server;

import com.ppp.rpc.config.RpcServiceConfig;
import com.ppp.rpc.constant.RpcConstant;
import com.ppp.rpc.factory.SingletonFactory;
import com.ppp.rpc.handler.RpcRequestHandler;
import com.ppp.rpc.monitor.RpcMonitor;
import com.ppp.rpc.provider.ServiceProvider;
import com.ppp.rpc.provider.impl.ZkServiceProvider;
import com.ppp.rpc.transmission.RpcServer;
import com.ppp.rpc.util.ShutdownHookUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyRpcServer implements RpcServer {
    private final int port;
    private final RpcRequestHandler rpcRequestHandler;
    private final ServiceProvider serviceProvider;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public NettyRpcServer() {
        this(RpcConstant.SERVER_PORT);
    }

    public NettyRpcServer(int port) {
        this(port, SingletonFactory.getInstance(ZkServiceProvider.class));
    }

    public NettyRpcServer(int port, ServiceProvider serviceProvider) {
        this.port = port;
        this.serviceProvider = serviceProvider;
        this.rpcRequestHandler = new RpcRequestHandler(serviceProvider);
    }

    @Override
    public void start() {
        ShutdownHookUtils.clearAll();
        ShutdownHookUtils.addShutdownHook(() -> {
            log.info("服务关闭，打印监控信息..");
            RpcMonitor.getInstance().printMetrics();
        });

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new ObjectEncoder());
                        pipeline.addLast(new ObjectDecoder(
                            ClassResolvers.cacheDisabled(null)));
                        pipeline.addLast(new NettyServerHandler(rpcRequestHandler));
                    }
                });

            ChannelFuture future = bootstrap.bind(port).sync();
            log.info("Netty服务启动, 端口: {}", port);

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("服务端异常", e);
        } finally {
            shutdown();
        }
    }

    @Override
    public void publishService(RpcServiceConfig config) {
        serviceProvider.publishService(config);
    }

    private void shutdown() {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
