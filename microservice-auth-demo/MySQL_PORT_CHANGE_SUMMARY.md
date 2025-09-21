# MySQL端口修改总结

## 🔧 修改内容

### 1. 端口变更
- **原端口**: 3306
- **新端口**: 3307
- **目的**: 避免与本地MySQL服务冲突

### 2. 修改的文件

| 文件 | 修改内容 |
|------|---------|
| `keycloak-config/docker-compose.yml` | 端口映射 `"3306:3306"` → `"3307:3306"` |
| `keycloak-config/init-db.sql` | 修复MySQL 8.0索引创建语法 |
| `README.md` | 更新端口信息为3307 |
| `START_GUIDE.md` | 更新文档和健康检查命令 |
| `QUICK_START.md` | 更新状态说明 |
| `database-config-example.yml` | 新建数据库配置示例 |

### 3. 数据库配置示例

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3307/microservice_db?useSSL=false&serverTimezone=UTC
    username: app_user
    password: app_password
    driver-class-name: com.mysql.cj.jdbc.Driver
```

## 🧪 验证结果

- ✅ MySQL容器在端口3307正常运行
- ✅ 数据库连接测试成功  
- ✅ 数据库初始化无语法错误
- ✅ 所有表和索引创建成功

## 📋 数据库信息

| 项目 | 值 |
|------|---|
| **主机端口** | 3307 |
| **容器内端口** | 3306 |
| **数据库名** | microservice_db |
| **应用用户** | app_user / app_password |
| **管理员** | root / root123 |

## 🚀 使用方法

### 1. 启动MySQL
```bash
cd keycloak-config
docker-compose up -d mysql
```

### 2. 连接测试
```bash
# 容器内连接测试
docker exec mysql-db mysqladmin ping -u root -proot123

# 外部连接测试  
mysql -h localhost -P 3307 -u app_user -papp_password microservice_db
```

### 3. Spring Boot配置
参考 `database-config-example.yml` 文件中的配置示例。

## ⚠️ 注意事项

1. 端口3307现在是MySQL的标准端口
2. 所有文档已更新反映新端口
3. 如有Spring Boot应用需要连接数据库，请使用端口3307
4. init-db.sql已修复MySQL 8.0兼容性问题

## 📞 故障排除

```bash
# 检查端口占用
lsof -i :3307

# 查看容器状态
docker-compose ps mysql

# 查看日志
docker-compose logs mysql
```

修改时间: $(date '+%Y-%m-%d %H:%M:%S')
