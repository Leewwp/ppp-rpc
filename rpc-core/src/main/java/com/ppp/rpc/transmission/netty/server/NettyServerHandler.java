package com.ppp.rpc.transmission.netty.server;

import com.ppp.rpc.dto.RpcRequest;
import com.ppp.rpc.dto.RpcResponse;
import com.ppp.rpc.enums.RpcResponseStatus;
import com.ppp.rpc.exception.RpcException;
import com.ppp.rpc.handler.RpcRequestHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class NettyServerHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private final RpcRequestHandler rpcRequestHandler;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest rpcRequest) {
        log.debug("收到RPC请求: {}", rpcRequest);

        try {
            Object data = rpcRequestHandler.invoke(rpcRequest);
            RpcResponse<?> rpcResponse = RpcResponse.success(rpcRequest.getRequestId(), data);
            ctx.writeAndFlush(rpcResponse);
            log.debug("RPC响应发送成功: {}", rpcResponse);
        } catch (RpcException e) {
            log.error("RPC调用异常: {}", e.getMessage(), e);
            RpcResponse<?> rpcResponse = RpcResponse.fail(rpcRequest.getRequestId(), e.getMessage());
            ctx.writeAndFlush(rpcResponse);
        } catch (Exception e) {
            log.error("服务端处理异常: {}", e.getMessage(), e);
            RpcResponse<?> rpcResponse = RpcResponse.fail(rpcRequest.getRequestId(), RpcResponseStatus.FAIL);
            ctx.writeAndFlush(rpcResponse);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("服务端异常: {}", cause.getMessage(), cause);
        ctx.close();
    }
}
