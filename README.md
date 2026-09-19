# ppp-rpc

[![English](https://img.shields.io/badge/English-2f81f7?style=flat-square)](README.md)
[![简体中文](https://img.shields.io/badge/简体中文-d0d7de?style=flat-square)](README.zh-CN.md)

![Java](https://img.shields.io/badge/Java-8-orange)
![Netty](https://img.shields.io/badge/Netty-4.1.77-00ADD8)
![ZooKeeper](https://img.shields.io/badge/ZooKeeper-Curator%204.2.0-red)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](./LICENSE)

A lightweight Java RPC framework. It re-implements the core mechanics of RPC in as little code as possible: dynamic proxies, network transport, service registration and discovery, load balancing, and retry/fault tolerance. Two transport implementations (Netty NIO / Socket BIO) share one set of abstractions, which makes them easy to study side by side.

## Features

- **Switchable transport**: `NettyRpcClient` / `NettyRpcServer` and `SocketRpcClient` / `SocketRpcServer`; the Socket client ships with a connection pool
- **Registry & discovery**: ZooKeeper-based service registration and discovery (`ZkServiceRegistry` / `ZkServiceDiscovery`), plus a registry-free `SimpleServiceProvider` direct-connection mode
- **Load balancing**: random, round-robin, and consistent-hashing strategies, switched uniformly through `LoadBalanceFactory`
- **Retry policies**: fixed-delay and exponential backoff (`RetryPolicy`), wrapped independently by `RetryableRpcClient`
- **Client proxy**: JDK dynamic proxy (`RpcClientProxy`) — remote calls read like local interface calls

## Modules

| Module | Description |
| --- | --- |
| `rpc-core` | framework core: transport, codec, registry/discovery, load balancing, retry, proxy |
| `test-api` | sample interfaces and models (`UserService`) |
| `test-server` | sample server: registers services and starts |
| `test-client` | sample client: invokes through the proxy |

## Quick start

Requirements: JDK 8+, Maven, and a reachable ZooKeeper (default `127.0.0.1:2181`).

```bash
mvn clean package
```

Start `test-server` first, then run `Main` in `test-client` to walk through the full register → discover → load-balance → invoke flow. Starting multiple `test-server` instances lets you watch load balancing at work.

## License

[MIT](./LICENSE)
