package com.ppp.rpc.exception;

public class RpcException extends RuntimeException {
    private final String errorCode;

    public RpcException() {
        super();
        this.errorCode = "UNKNOWN_ERROR";
    }

    public RpcException(String message) {
        super(message);
        this.errorCode = "UNKNOWN_ERROR";
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "UNKNOWN_ERROR";
    }

    public RpcException(Throwable cause) {
        super(cause);
        this.errorCode = "UNKNOWN_ERROR";
    }

    public RpcException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public RpcException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    // 预定义的异常类型
    public static class ServiceNotFoundException extends RpcException {
        public ServiceNotFoundException(String message) {
            super("SERVICE_NOT_FOUND", message);
        }

        public ServiceNotFoundException(String message, Throwable cause) {
            super("SERVICE_NOT_FOUND", message, cause);
        }
    }

    public static class MethodNotFoundException extends RpcException {
        public MethodNotFoundException(String message) {
            super("METHOD_NOT_FOUND", message);
        }

        public MethodNotFoundException(String message, Throwable cause) {
            super("METHOD_NOT_FOUND", message, cause);
        }
    }

    public static class NetworkException extends RpcException {
        public NetworkException(String message) {
            super("NETWORK_ERROR", message);
        }

        public NetworkException(String message, Throwable cause) {
            super("NETWORK_ERROR", message, cause);
        }
    }

    public static class SerializationException extends RpcException {
        public SerializationException(String message) {
            super("SERIALIZATION_ERROR", message);
        }

        public SerializationException(String message, Throwable cause) {
            super("SERIALIZATION_ERROR", message, cause);
        }
    }

    public static class RegistryException extends RpcException {
        public RegistryException(String message) {
            super("REGISTRY_ERROR", message);
        }

        public RegistryException(String message, Throwable cause) {
            super("REGISTRY_ERROR", message, cause);
        }
    }
}
