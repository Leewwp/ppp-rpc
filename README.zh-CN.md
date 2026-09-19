# ppp-rpc

[![English](https://img.shields.io/badge/English-2f81f7?style=flat-square)](README.md)
[![简体中文](https://img.shields.io/badge/简体中文-d0d7de?style=flat-square)](README.zh-CN.md)

![Java](https://img.shields.io/badge/Java-8-orange)
![Netty](https://img.shields.io/badge/Netty-4.1.77-00ADD8)
![ZooKeeper](https://img.shields.io/badge/ZooKeeper-Curator%204.2.0-red)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](./LICENSE)

轻量级 Java RPC 框架。用尽量少的代码把 RPC 的核心机制实现一遍：动态代理、网络传输、服务注册发现、负载均衡、重试容错。两套传输实现（Netty NIO / Socket BIO）共用同一套抽象，便于对照学习。

## 特性

- **传输层可切换**：`NettyRpcClient` / `NettyRpcServer` 与 `SocketRpcClient` / `SocketRpcServer` 两套实现，Socket 客户端带连接池
- **注册发现**：基于 ZooKeeper 的服务注册与发现（`ZkServiceRegistry` / `ZkServiceDiscovery`），另有无注册中心的 `SimpleServiceProvider` 直连模式
- **负载均衡**：随机、轮询、一致性哈希三种策略，`LoadBalanceFactory` 统一切换
- **重试策略**：固定间隔与指数退避（`RetryPolicy`），`RetryableRpcClient` 独立包装
- **客户端代理**：JDK 动态代理（`RpcClientProxy`），远程调用写起来和本地接口一致

## 模块

| 模块 | 说明 |
| --- | --- |
| `rpc-core` | 框架核心：传输、编解码、注册发现、负载均衡、重试、代理 |
| `test-api` | 示例接口与模型（`UserService`） |
| `test-server` | 服务端示例：注册服务并启动 |
| `test-client` | 客户端示例：通过代理发起调用 |

## 快速开始

环境要求：JDK 8+、Maven、可用的 ZooKeeper（默认 `127.0.0.1:2181`）。

```bash
mvn clean package
```

先启动 `test-server`，再运行 `test-client` 的 `Main`，走一遍注册 → 发现 → 负载均衡 → 调用的完整流程。启动多个 `test-server` 实例可以观察负载均衡效果。

## License

[MIT](./LICENSE)
