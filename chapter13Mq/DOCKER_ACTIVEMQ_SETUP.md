# Docker ActiveMQ 安装和配置指南

## 🎯 概述

本指南将帮助您使用 Docker 快速安装和配置 Apache ActiveMQ Artemis，为 Spring Boot 项目提供消息队列服务。

## 📋 前置要求

- Docker Desktop 已安装并运行
- Docker Compose 已安装
- 至少 2GB 可用内存

## 🚀 快速开始

### 1. 启动 ActiveMQ

在项目根目录下运行：

```bash
# 启动 ActiveMQ
./start-activemq.sh start

# 或者直接使用 docker-compose
docker-compose up -d
```

### 2. 验证安装

```bash
# 检查容器状态
./start-activemq.sh status

# 或者
docker-compose ps
```

### 3. 访问 Web 控制台

打开浏览器访问：`http://localhost:8161`

- **用户名**: `admin`
- **密码**: `admin`

## 🔧 配置详情

### Docker Compose 配置

```yaml
version: '3.8'

services:
  activemq:
    image: apache/activemq-artemis:latest
    container_name: activemq-artemis
    ports:
      - "61616:61616"  # OpenWire 端口 (JMS 客户端连接)
      - "8161:8161"    # Web 控制台端口
      - "5672:5672"    # AMQP 端口
      - "1883:1883"    # MQTT 端口
      - "61613:61613"  # STOMP 端口
    environment:
      - ARTEMIS_USERNAME=admin
      - ARTEMIS_PASSWORD=admin
      - ANONYMOUS_LOGIN=true
    volumes:
      - activemq_data:/var/lib/artemis-instance
    networks:
      - activemq-network
    restart: unless-stopped
```

### 端口说明

| 端口 | 协议 | 用途 |
|------|------|------|
| 61616 | OpenWire | JMS 客户端连接 (Spring Boot 使用) |
| 8161 | HTTP | Web 管理控制台 |
| 5672 | AMQP | AMQP 协议连接 |
| 1883 | MQTT | MQTT 协议连接 |
| 61613 | STOMP | STOMP 协议连接 |

## 🛠️ 管理命令

### 使用管理脚本

```bash
# 启动 ActiveMQ
./start-activemq.sh start

# 停止 ActiveMQ
./start-activemq.sh stop

# 重启 ActiveMQ
./start-activemq.sh restart

# 查看状态
./start-activemq.sh status

# 查看日志
./start-activemq.sh logs

# 清理数据
./start-activemq.sh clean
```

### 直接使用 Docker Compose

```bash
# 启动服务
docker-compose up -d

# 停止服务
docker-compose down

# 重启服务
docker-compose restart

# 查看状态
docker-compose ps

# 查看日志
docker-compose logs -f activemq

# 清理数据
docker-compose down -v
```

## 🔗 Spring Boot 集成

### 1. 更新 application.yml

确保您的 Spring Boot 项目配置正确：

```yaml
spring:
  activemq:
    broker-url: tcp://localhost:61616
    user: admin
    password: admin
    pool:
      enabled: true
      max-connections: 10
```

### 2. 测试连接

运行您的 Spring Boot 应用：

```bash
cd chapter13Mq
./run-demo.sh
```

### 3. 测试 API

```bash
cd chapter13Mq
./test-api.sh
```

## 📊 监控和管理

### Web 控制台功能

访问 `http://localhost:8161` 可以：

- 查看队列和主题
- 监控消息流量
- 管理连接
- 查看系统状态
- 发送测试消息

### 常用监控指标

- **队列深度**: 队列中待处理的消息数量
- **消费者数量**: 连接到队列的消费者数量
- **消息吞吐量**: 每秒处理的消息数量
- **连接数**: 当前活跃的连接数量

## 🐛 故障排除

### 常见问题

1. **容器启动失败**
   ```bash
   # 检查 Docker 是否运行
   docker info
   
   # 查看容器日志
   docker-compose logs activemq
   ```

2. **端口冲突**
   ```bash
   # 检查端口占用
   lsof -i :61616
   lsof -i :8161
   ```

3. **连接被拒绝**
   ```bash
   # 等待容器完全启动
   sleep 30
   
   # 检查容器状态
   docker-compose ps
   ```

4. **内存不足**
   ```bash
   # 检查系统内存
   free -h
   
   # 增加 Docker 内存限制
   # 在 Docker Desktop 设置中调整
   ```

### 日志查看

```bash
# 查看实时日志
docker-compose logs -f activemq

# 查看最近 100 行日志
docker-compose logs --tail=100 activemq
```

## 🔒 安全配置

### 生产环境建议

1. **更改默认密码**
   ```yaml
   environment:
     - ARTEMIS_USERNAME=your_username
     - ARTEMIS_PASSWORD=your_strong_password
   ```

2. **禁用匿名登录**
   ```yaml
   environment:
     - ANONYMOUS_LOGIN=false
   ```

3. **使用 TLS 加密**
   ```yaml
   ports:
     - "61617:61617"  # TLS 端口
   ```

## 📈 性能优化

### 内存配置

```yaml
environment:
  - ARTEMIS_OPTS=-Xms512m -Xmx2g
```

### 连接池配置

```yaml
environment:
  - ARTEMIS_OPTS=-Dactivemq.connectionPool.maxConnections=100
```

## 🎉 完成

现在您已经成功安装了 ActiveMQ，可以开始使用 Spring Boot 进行消息队列开发了！

### 下一步

1. 运行 `chapter13Mq` 项目
2. 测试消息发送和接收
3. 查看 ActiveMQ Web 控制台
4. 探索更多消息队列功能

### 有用的链接

- [ActiveMQ Artemis 官方文档](https://activemq.apache.org/components/artemis/documentation/)
- [Spring Boot JMS 文档](https://docs.spring.io/spring-boot/docs/current/reference/html/messaging.html#messaging.jms)
- [Docker Compose 文档](https://docs.docker.com/compose/)
