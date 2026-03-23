package com.ppp.rpc.retry;

import com.ppp.rpc.dto.RpcRequest;
import com.ppp.rpc.dto.RpcResponse;
import com.ppp.rpc.exception.RpcException;
import com.ppp.rpc.transmission.RpcClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class RetryableRpcClient implements RpcClient {
    private final RpcClient delegate;
    private final RetryPolicy retryPolicy;

    public RetryableRpcClient(RpcClient delegate) {
        this(delegate, new ExponentialBackoffRetryPolicy());
    }

    @Override
    public RpcResponse<?> sendReq(RpcRequest rpcRequest) {
        int attempt = 0;
        RpcException lastException = null;

        while (true) {
            try {
                return delegate.sendReq(rpcRequest);
            } catch (RpcException e) {
                lastException = e;
                if (!retryPolicy.shouldRetry(attempt, e)) {
                    throw e;
                }

                long delay = retryPolicy.getRetryDelay(attempt);
                attempt++;

                log.warn("RPC request failed (attempt {}), retrying in {}ms: {}",
                    attempt, delay, e.getMessage());

                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RpcException("Retry interrupted", ie);
                }
            }
        }
    }
}
