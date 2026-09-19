package com.ppp.rpc.retry;

import com.ppp.rpc.exception.RpcException;

public interface RetryPolicy {
    boolean shouldRetry(int attempt, RpcException exception);
    long getRetryDelay(int attempt);
}
