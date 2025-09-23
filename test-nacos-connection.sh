#!/bin/bash

echo "=========================================="
echo "Nacos 连接测试脚本"
echo "=========================================="

# 检查 Nacos 容器状态
echo "1. 检查 Nacos 容器状态..."
if podman ps | grep -q nacos-server; then
    echo "✅ Nacos 容器正在运行"
else
    echo "❌ Nacos 容器未运行"
    exit 1
fi

# 等待 Nacos 启动
echo ""
echo "2. 等待 Nacos 服务启动..."
for i in {1..30}; do
    if curl -s http://localhost:8848/nacos/ | grep -q "nacos"; then
        echo "✅ Nacos Web 界面可访问"
        break
    else
        echo "⏳ 等待中... ($i/30)"
        sleep 2
    fi
done

# 测试认证
echo ""
echo "3. 测试 Nacos 认证..."
TOKEN=$(curl -s -X POST "http://localhost:8848/nacos/v1/auth/login" \
  -d "username=nacos&password=nacos" | \
  grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)

if [ -n "$TOKEN" ]; then
    echo "✅ Nacos 认证成功"
    echo "   访问令牌: ${TOKEN:0:20}..."
else
    echo "❌ Nacos 认证失败"
    exit 1
fi

# 测试服务注册
echo ""
echo "4. 测试服务注册..."
if curl -s -X POST "http://localhost:8848/nacos/v1/ns/instance" \
  -d "serviceName=test-service" \
  -d "ip=127.0.0.1" \
  -d "port=9999" \
  -d "accessToken=$TOKEN" | grep -q "ok"; then
    echo "✅ 服务注册功能正常"
else
    echo "❌ 服务注册功能异常"
fi

# 测试服务发现
echo ""
echo "5. 测试服务发现..."
if curl -s "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=test-service&accessToken=$TOKEN" | \
  grep -q "127.0.0.1"; then
    echo "✅ 服务发现功能正常"
else
    echo "❌ 服务发现功能异常"
fi

# 清理测试服务
echo ""
echo "6. 清理测试服务..."
curl -s -X DELETE "http://localhost:8848/nacos/v1/ns/instance" \
  -d "serviceName=test-service" \
  -d "ip=127.0.0.1" \
  -d "port=9999" \
  -d "accessToken=$TOKEN" > /dev/null
echo "✅ 测试服务已清理"

echo ""
echo "=========================================="
echo "测试完成！"
echo "=========================================="
echo ""
echo "现在您可以启动微服务应用："
echo "1. cd microservice-auth-demo/auth-service && mvn spring-boot:run"
echo "2. cd microservice-auth-demo/user-service && mvn spring-boot:run"
echo "3. cd microservice-auth-demo/gateway && mvn spring-boot:run"
echo ""
echo "访问 Nacos 控制台: http://localhost:8848/nacos"
echo "用户名/密码: nacos/nacos"
