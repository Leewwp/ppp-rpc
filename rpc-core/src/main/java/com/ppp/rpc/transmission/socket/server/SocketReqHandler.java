package com.ppp.rpc.transmission.socket.server;

import com.ppp.rpc.dto.RpcRequest;
import com.ppp.rpc.dto.RpcResponse;
import com.ppp.rpc.enums.RpcResponseStatus;
import com.ppp.rpc.exception.RpcException;
import com.ppp.rpc.handler.RpcRequestHandler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Slf4j
@AllArgsConstructor
public class SocketReqHandler implements Runnable {
    private final Socket socket;
    private final RpcRequestHandler rpcRequestHandler;

    @Override
    public void run() {
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(socket.getInputStream());
             ObjectInputStream inputStream = new ObjectInputStream(bufferedInputStream);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(socket.getOutputStream());
             ObjectOutputStream outputStream = new ObjectOutputStream(bufferedOutputStream)) {
            RpcRequest rpcRequest = (RpcRequest) inputStream.readObject();
            log.debug("收到RPC请求: {}", rpcRequest);

            Object data;
            try {
                data = rpcRequestHandler.invoke(rpcRequest);
                RpcResponse<?> rpcResponse = RpcResponse.success(rpcRequest.getRequestId(), data);
                outputStream.writeObject(rpcResponse);
                outputStream.flush();
                bufferedOutputStream.flush(); // 确保数据被发送
                log.debug("RPC响应发送成功: {}", rpcResponse);
            } catch (RpcException e) {
                log.error("RPC调用异常: {}", e.getMessage(), e);
                RpcResponse<?> rpcResponse = RpcResponse.fail(rpcRequest.getRequestId(), e.getMessage());
                outputStream.writeObject(rpcResponse);
                outputStream.flush();
                bufferedOutputStream.flush(); // 确保数据被发送
            } catch (Exception e) {
                log.error("服务端处理异常: {}", e.getMessage(), e);
                RpcResponse<?> rpcResponse = RpcResponse.fail(rpcRequest.getRequestId(), RpcResponseStatus.FAIL);
                outputStream.writeObject(rpcResponse);
                outputStream.flush();
                bufferedOutputStream.flush(); // 确保数据被发送
            }
        } catch (Exception e) {
            log.error("Socket处理异常: {}", e.getMessage(), e);
        }
    }
}
