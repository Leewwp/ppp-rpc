package com.ppp.rpc.monitor;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class RpcMonitor {
    private static final RpcMonitor INSTANCE = new RpcMonitor();
    private final ConcurrentMap<String, MethodMetrics> methodMetricsMap = new ConcurrentHashMap<>();

    private RpcMonitor() {
    }

    public static RpcMonitor getInstance() {
        return INSTANCE;
    }

    public void recordMethodCall(String methodName) {
        MethodMetrics metrics = methodMetricsMap.computeIfAbsent(methodName, k -> new MethodMetrics());
        metrics.incrementCallCount();
    }

    public void recordMethodTime(String methodName, long elapsedTime) {
        MethodMetrics metrics = methodMetricsMap.computeIfAbsent(methodName, k -> new MethodMetrics());
        metrics.addElapsedTime(elapsedTime);
    }

    public void recordError(String methodName) {
        MethodMetrics metrics = methodMetricsMap.computeIfAbsent(methodName, k -> new MethodMetrics());
        metrics.incrementErrorCount();
    }

    public void printMetrics() {
        log.info("RPC 方法调用统计:");
        methodMetricsMap.forEach((methodName, metrics) -> {
            log.info("方法: {}, 调用次数: {}, 错误次数: {}, 平均耗时: {}ms",
                    methodName,
                    metrics.getCallCount(),
                    metrics.getErrorCount(),
                    metrics.getAverageElapsedTime());
        });
    }

    private static class MethodMetrics {
        private final AtomicLong callCount = new AtomicLong(0);
        private final AtomicLong errorCount = new AtomicLong(0);
        private final AtomicLong totalElapsedTime = new AtomicLong(0);

        public void incrementCallCount() {
            callCount.incrementAndGet();
        }

        public void incrementErrorCount() {
            errorCount.incrementAndGet();
        }

        public void addElapsedTime(long elapsedTime) {
            totalElapsedTime.addAndGet(elapsedTime);
        }

        public long getCallCount() {
            return callCount.get();
        }

        public long getErrorCount() {
            return errorCount.get();
        }

        public long getTotalElapsedTime() {
            return totalElapsedTime.get();
        }

        public double getAverageElapsedTime() {
            long count = callCount.get();
            if (count == 0) {
                return 0;
            }
            return (double) totalElapsedTime.get() / count;
        }
    }
}
