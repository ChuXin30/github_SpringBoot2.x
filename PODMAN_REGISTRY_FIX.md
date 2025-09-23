# Podman 注册表连接问题解决方案

## 🚨 问题描述

在使用 Podman 搜索镜像时遇到网络连接问题：

```bash
podman search mysql
Error: 1 error occurred:
	* couldn't search registry "docker.io": pinging container registry index.docker.io: Get "https://index.docker.io/v2/": dial tcp 157.240.2.14:443: i/o timeout
```

## 🔧 解决方案

### 方案 1: 配置国内镜像源（推荐）

创建或编辑 Podman 注册表配置文件：

```bash
mkdir -p ~/.config/containers
```

创建 `~/.config/containers/registries.conf` 文件：

```ini
unqualified-search-registries = ["docker.io", "registry.fedoraproject.org", "quay.io"]

[[registry]]
prefix = "docker.io"
location = "docker.io"
insecure = false
blocked = false

[[registry.mirror]]
location = "docker.mirrors.ustc.edu.cn"

[[registry.mirror]]
location = "hub-mirror.c.163.com"

[[registry.mirror]]
location = "mirror.baidubce.com"
```

### 方案 2: 直接使用已知镜像

由于网络问题，可以直接使用已知的镜像名称：

```bash
# 直接拉取 MySQL 镜像
podman pull mysql:8.0

# 或者使用完整路径
podman pull docker.io/library/mysql:8.0
```

### 方案 3: 使用本地镜像

如果之前已经下载过镜像，可以使用本地镜像：

```bash
# 查看本地镜像
podman images

# 使用本地镜像启动容器
podman run -d --name mysql-db mysql:8.0
```

## 🚀 快速启动 MySQL

### 使用管理脚本

```bash
# 启动 MySQL 容器
./mysql-container-manager.sh start

# 查看状态
./mysql-container-manager.sh status

# 测试连接
./mysql-container-manager.sh test

# 连接到 MySQL
./mysql-container-manager.sh connect
```

### 手动启动

```bash
podman run -d --name mysql-db \
  -e MYSQL_ROOT_PASSWORD=root123 \
  -e MYSQL_DATABASE=microservice_db \
  -e MYSQL_USER=app_user \
  -e MYSQL_PASSWORD=app_password \
  -p 3307:3306 \
  mysql:8.0
```

## 📊 连接信息

| 项目 | 值 |
|------|-----|
| 主机 | localhost |
| 端口 | 3307 |
| 根用户 | root |
| 根密码 | root123 |
| 应用用户 | app_user |
| 应用密码 | app_password |
| 数据库 | microservice_db |

## 🔍 故障排除

### 1. 检查网络连接

```bash
# 测试网络连接
ping -c 3 index.docker.io

# 测试 DNS 解析
nslookup index.docker.io
```

### 2. 检查防火墙设置

```bash
# 检查防火墙状态
sudo ufw status

# 临时关闭防火墙（仅用于测试）
sudo ufw disable
```

### 3. 使用代理

如果使用代理，配置 Podman：

```bash
export HTTP_PROXY=http://proxy.example.com:8080
export HTTPS_PROXY=http://proxy.example.com:8080
export NO_PROXY=localhost,127.0.0.1
```

### 4. 检查 Podman 配置

```bash
# 查看 Podman 配置
podman info

# 查看注册表配置
cat ~/.config/containers/registries.conf
```

## 🎯 验证解决方案

### 测试镜像搜索

```bash
# 测试搜索功能
podman search mysql

# 测试拉取镜像
podman pull mysql:8.0
```

### 测试容器运行

```bash
# 启动测试容器
podman run --rm -it alpine:latest echo "Hello Podman!"

# 检查容器状态
podman ps -a
```

## 📚 相关资源

- [Podman 官方文档](https://podman.io/docs/)
- [容器注册表配置](https://github.com/containers/image/blob/main/docs/containers-registries.conf.5.md)
- [Docker Hub 镜像源](https://hub.docker.com/)

## ✅ 成功标志

当您看到以下输出时，说明问题已解决：

```bash
$ podman search mysql
INDEX       NAME                                    DESCRIPTION                                       STARS       OFFICIAL   AUTOMATED
docker.io   docker.io/library/mysql                 MySQL is a widely used, open-source relation...   15000+      [OK]       
docker.io   docker.io/library/mariadb               MariaDB is a community-developed fork of MyS...   5000+       [OK]       
...
```

现在您可以正常使用 Podman 搜索和拉取镜像了！
