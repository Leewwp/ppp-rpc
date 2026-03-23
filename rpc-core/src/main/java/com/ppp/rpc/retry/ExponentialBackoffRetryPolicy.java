package com.ppp.rpc.retry;

import com.ppp.rpc.exception.RpcException;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ExponentialBackoffRetryPolicy implements RetryPolicy {
    private final int maxRetries;
    private final long baseDelayMs;
    private final long maxDelayMs;

    public ExponentialBackoffRetryPolicy() {
        this(3, 500, 5000);
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
        long delay = baseDelayMs * (long) Math.pow(2, attempt);
        return Math.min(delay, maxDelayMs);
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
