#!/bin/bash

echo "=========================================="
echo "微服务启动脚本（无 Nacos 注册）"
echo "=========================================="

# 检查基础服务状态
echo "检查基础服务状态..."
echo "MySQL: $(curl -s http://localhost:3307 > /dev/null && echo "✅ 可访问" || echo "❌ 不可访问")"
echo "Redis: $(redis-cli -h localhost -p 6379 -a redis123 ping 2>/dev/null && echo "✅ 可访问" || echo "❌ 不可访问")"
echo "Keycloak: $(curl -s http://localhost:8180 > /dev/null && echo "✅ 可访问" || echo "❌ 不可访问")"
echo "Nacos: $(curl -s http://localhost:8848/nacos/ > /dev/null && echo "✅ 可访问" || echo "❌ 不可访问")"
echo ""

# 启动认证服务
echo "1. 启动认证服务 (auth-service:8081)..."
cd /Users/hanggao/github_code/SpringBoot2.x/microservice-auth-demo/auth-service
nohup mvn spring-boot:run > auth-service.log 2>&1 &
AUTH_PID=$!
echo "   认证服务 PID: $AUTH_PID"
echo "   日志文件: auth-service.log"

# 等待认证服务启动
echo "   等待认证服务启动..."
sleep 30

# 检查认证服务状态
if curl -s http://localhost:8081/actuator/health > /dev/null; then
    echo "   ✅ 认证服务启动成功"
else
    echo "   ❌ 认证服务启动失败"
fi

# 启动用户服务
echo ""
echo "2. 启动用户服务 (user-service:8082)..."
cd /Users/hanggao/github_code/SpringBoot2.x/microservice-auth-demo/user-service
nohup mvn spring-boot:run > user-service.log 2>&1 &
USER_PID=$!
echo "   用户服务 PID: $USER_PID"
echo "   日志文件: user-service.log"

# 等待用户服务启动
echo "   等待用户服务启动..."
sleep 30

# 检查用户服务状态
if curl -s http://localhost:8082/actuator/health > /dev/null; then
    echo "   ✅ 用户服务启动成功"
else
    echo "   ❌ 用户服务启动失败"
fi

# 启动网关服务
echo ""
echo "3. 启动网关服务 (api-gateway:8080)..."
cd /Users/hanggao/github_code/SpringBoot2.x/microservice-auth-demo/gateway
nohup mvn spring-boot:run > gateway.log 2>&1 &
GATEWAY_PID=$!
echo "   网关服务 PID: $GATEWAY_PID"
echo "   日志文件: gateway.log"

# 等待网关服务启动
echo "   等待网关服务启动..."
sleep 30

# 检查网关服务状态
if curl -s http://localhost:8080/actuator/health > /dev/null; then
    echo "   ✅ 网关服务启动成功"
else
    echo "   ❌ 网关服务启动失败"
fi

echo ""
echo "=========================================="
echo "服务启动完成！"
echo "=========================================="
echo ""
echo "服务状态："
echo "  认证服务: http://localhost:8081 (PID: $AUTH_PID)"
echo "  用户服务: http://localhost:8082 (PID: $USER_PID)"
echo "  网关服务: http://localhost:8080 (PID: $GATEWAY_PID)"
echo ""
echo "测试端点："
echo "  网关健康检查: http://localhost:8080/actuator/health"
echo "  认证服务健康检查: http://localhost:8081/actuator/health"
echo "  用户服务健康检查: http://localhost:8082/actuator/health"
echo ""
echo "API 测试："
echo "  认证 API: http://localhost:8080/auth/health"
echo "  用户 API: http://localhost:8080/api/user/health"
echo ""
echo "日志文件："
echo "  认证服务: microservice-auth-demo/auth-service/auth-service.log"
echo "  用户服务: microservice-auth-demo/user-service/user-service.log"
echo "  网关服务: microservice-auth-demo/gateway/gateway.log"
echo ""
echo "停止服务："
echo "  kill $AUTH_PID $USER_PID $GATEWAY_PID"
echo ""
echo "注意：当前配置已禁用 Nacos 服务注册，使用直连路由。"
echo "当 Nacos 完全启动后，可以重新启用服务注册。"
