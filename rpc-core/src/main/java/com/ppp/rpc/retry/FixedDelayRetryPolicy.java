package com.ppp.rpc.retry;

import com.ppp.rpc.exception.RpcException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class FixedDelayRetryPolicy implements RetryPolicy {
    private final int maxRetries;
    private final long delayMs;

    public FixedDelayRetryPolicy() {
        this(3, 1000);
    }

    @Override
    public boolean shouldRetry(int attempt, RpcException exception) {
        if (attempt >= maxRetries) {
            return false;
        }
        return isRetryable(exception);
    }

    @Override
    public long getRetryDelay(int attempt) {
        return delayMs;
    }

    private boolean isRetryable(RpcException exception) {
        if (exception instanceof RpcException.NetworkException) {
            return true;
        }
        if (exception instanceof RpcException.RegistryException) {
            return true;
        }
        return false;
    }
}
