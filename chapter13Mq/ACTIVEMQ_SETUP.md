# ActiveMQ 安装和配置指南

## ActiveMQ 简介

Apache ActiveMQ是一个开源的消息中间件，支持多种消息传递协议和模式。它是Java Message Service (JMS) 的一个实现，提供了可靠的消息传递功能。

## 安装ActiveMQ

### 1. 下载ActiveMQ

访问 [Apache ActiveMQ官网](http://activemq.apache.org/downloads) 下载最新版本：

```bash
# 下载ActiveMQ (以5.17.0版本为例)
wget https://archive.apache.org/dist/activemq/5.17.0/apache-activemq-5.17.0-bin.tar.gz

# 解压
tar -xzf apache-activemq-5.17.0-bin.tar.gz
cd apache-activemq-5.17.0
```

### 2. 启动ActiveMQ

```bash
# 启动ActiveMQ
bin/activemq start

# 查看状态
bin/activemq status

# 停止ActiveMQ
bin/activemq stop
```

### 3. 验证安装

- **Web控制台**: http://localhost:8161/admin
- **用户名/密码**: admin/admin
- **Broker URL**: tcp://localhost:61616

## Docker方式安装

### 1. 使用Docker运行ActiveMQ

```bash
# 拉取ActiveMQ镜像
docker pull apache/activemq-artemis:latest

# 运行ActiveMQ容器
docker run -d \
  --name activemq \
  -p 61616:61616 \
  -p 8161:8161 \
  -e ACTIVEMQ_ADMIN_LOGIN=admin \
  -e ACTIVEMQ_ADMIN_PASSWORD=admin \
  apache/activemq-artemis:latest
```

### 2. 使用Docker Compose

创建 `docker-compose.yml` 文件：

```yaml
version: '3.8'
services:
  activemq:
    image: apache/activemq-artemis:latest
    container_name: activemq
    ports:
      - "61616:61616"  # OpenWire端口
      - "8161:8161"    # Web控制台端口
    environment:
      - ACTIVEMQ_ADMIN_LOGIN=admin
      - ACTIVEMQ_ADMIN_PASSWORD=admin
    volumes:
      - activemq_data:/var/lib/activemq
    restart: unless-stopped

volumes:
  activemq_data:
```

启动服务：

```bash
docker-compose up -d
```

## 配置说明

### 1. 基本配置

ActiveMQ的主要配置文件位于 `conf/activemq.xml`：

```xml
<broker xmlns="http://activemq.apache.org/schema/core" brokerName="localhost" dataDirectory="${activemq.data}">
    <transportConnectors>
        <transportConnector name="openwire" uri="tcp://0.0.0.0:61616"/>
        <transportConnector name="stomp" uri="stomp://0.0.0.0:61613"/>
        <transportConnector name="amqp" uri="amqp://0.0.0.0:5672"/>
        <transportConnector name="mqtt" uri="mqtt://0.0.0.0:1883"/>
    </transportConnectors>
</broker>
```

### 2. 持久化配置

ActiveMQ支持多种持久化方式：

#### KahaDB (默认)
```xml
<persistenceAdapter>
    <kahaDB directory="${activemq.data}/kahadb"/>
</persistenceAdapter>
```

#### JDBC
```xml
<persistenceAdapter>
    <jdbcPersistenceAdapter dataSource="#mysql-ds"/>
</jdbcPersistenceAdapter>
```

### 3. 安全配置

#### 用户认证
在 `conf/users.properties` 中配置用户：

```properties
# 用户名=密码
admin=admin
user=password
```

#### 权限配置
在 `conf/groups.properties` 中配置用户组：

```properties
# 组名=用户列表
admins=admin
users=user
```

在 `conf/activemq.xml` 中配置权限：

```xml
<authorizationPlugin>
    <map>
        <authorizationMap>
            <authorizationEntries>
                <authorizationEntry queue=">" read="users" write="users" admin="users"/>
                <authorizationEntry topic=">" read="users" write="users" admin="users"/>
            </authorizationEntries>
        </authorizationMap>
    </map>
</authorizationPlugin>
```

## 监控和管理

### 1. Web控制台

访问 http://localhost:8161/admin 可以：

- 查看队列和主题状态
- 监控消息数量
- 管理连接和会话
- 查看系统信息

### 2. JMX监控

ActiveMQ支持JMX监控，可以通过JConsole或其他JMX客户端连接：

```bash
# 启动JConsole
jconsole

# 连接URL: service:jmx:rmi:///jndi/rmi://localhost:1099/jmxrmi
```

### 3. 日志监控

ActiveMQ日志位于 `data/activemq.log`：

```bash
# 查看日志
tail -f data/activemq.log

# 查看错误日志
grep ERROR data/activemq.log
```

## 性能优化

### 1. JVM参数优化

在 `bin/activemq` 脚本中设置JVM参数：

```bash
# 内存设置
ACTIVEMQ_OPTS="$ACTIVEMQ_OPTS -Xms512m -Xmx1024m"

# GC设置
ACTIVEMQ_OPTS="$ACTIVEMQ_OPTS -XX:+UseG1GC"

# 其他优化参数
ACTIVEMQ_OPTS="$ACTIVEMQ_OPTS -XX:+UseStringDeduplication"
```

### 2. 连接池配置

在Spring Boot应用中配置连接池：

```yaml
spring:
  activemq:
    pool:
      enabled: true
      max-connections: 10
      max-active-session-per-connection: 500
      block-if-session-pool-is-full: true
      idle-timeout: 30000
```

### 3. 消息优化

- 使用消息压缩
- 优化消息大小
- 合理设置消息过期时间
- 使用批量操作

## 常见问题

### 1. 连接问题

**问题**: 无法连接到ActiveMQ
**解决方案**:
- 检查ActiveMQ是否启动
- 验证端口是否正确
- 检查防火墙设置
- 查看网络连接

### 2. 内存问题

**问题**: ActiveMQ内存不足
**解决方案**:
- 增加JVM内存
- 优化消息大小
- 清理无用消息
- 调整GC参数

### 3. 性能问题

**问题**: 消息处理缓慢
**解决方案**:
- 增加并发数
- 优化网络配置
- 使用连接池
- 调整消息持久化策略

### 4. 消息丢失

**问题**: 消息丢失
**解决方案**:
- 启用消息持久化
- 使用事务
- 实现消息确认机制
- 检查消费者状态

## 集群配置

### 1. 主从复制

```xml
<broker xmlns="http://activemq.apache.org/schema/core" brokerName="broker1" dataDirectory="${activemq.data}">
    <persistenceAdapter>
        <kahaDB directory="${activemq.data}/kahadb"/>
    </persistenceAdapter>
    <networkConnectors>
        <networkConnector uri="static:(tcp://broker2:61616)"/>
    </networkConnectors>
</broker>
```

### 2. 负载均衡

```xml
<broker xmlns="http://activemq.apache.org/schema/core" brokerName="broker1" dataDirectory="${activemq.data}">
    <networkConnectors>
        <networkConnector uri="static:(tcp://broker2:61616,tcp://broker3:61616)" 
                         duplex="false" 
                         failover="true"/>
    </networkConnectors>
</broker>
```

## 总结

ActiveMQ是一个功能强大的消息中间件，通过合理的配置和优化，可以提供可靠的消息传递服务。本指南涵盖了ActiveMQ的安装、配置、监控和优化等方面，为实际应用提供参考。

在使用ActiveMQ时，建议：

1. 根据实际需求选择合适的持久化方式
2. 合理配置安全策略
3. 监控系统性能
4. 定期备份数据
5. 关注日志和错误信息
