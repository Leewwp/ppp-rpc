package com.ppp.rpc.transmission.netty.client;

import com.ppp.rpc.dto.RpcRequest;
import com.ppp.rpc.dto.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<RpcResponse<?>> {
    private final ConcurrentHashMap<String, RpcResponse<?>> responseMap;
    private final ConcurrentHashMap<String, CountDownLatch> latchMap;

    public NettyClientHandler(ConcurrentHashMap<String, RpcResponse<?>> responseMap,
                              ConcurrentHashMap<String, CountDownLatch> latchMap) {
        this.responseMap = responseMap;
        this.latchMap = latchMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse<?> response) {
        String requestId = response.getRequestId();
        log.debug("收到RPC响应: requestId={}", requestId);

        responseMap.put(requestId, response);
        CountDownLatch latch = latchMap.get(requestId);
        if (latch != null) {
            latch.countDown();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("客户端异常: {}", cause.getMessage(), cause);
        ctx.close();
    }
}
