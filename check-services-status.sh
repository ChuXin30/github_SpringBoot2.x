#!/bin/bash

echo "=========================================="
echo "微服务环境状态检查"
echo "=========================================="

# 检查容器服务
echo "📦 容器服务状态："
echo "MySQL: $(podman ps | grep mysql-db | grep -q Up && echo "✅ 运行中" || echo "❌ 未运行")"
echo "Redis: $(podman ps | grep redis-cache | grep -q Up && echo "✅ 运行中" || echo "❌ 未运行")"
echo "Keycloak: $(podman ps | grep keycloak-auth-server | grep -q Up && echo "✅ 运行中" || echo "❌ 未运行")"
echo "Nacos: $(podman ps | grep nacos-server | grep -q Up && echo "✅ 运行中" || echo "❌ 未运行")"
echo ""

# 检查服务连接
echo "🔗 服务连接状态："
echo "MySQL (3307): $(curl -s http://localhost:3307 > /dev/null 2>&1 && echo "✅ 可访问" || echo "❌ 不可访问")"
echo "Redis (6379): $(redis-cli -h localhost -p 6379 -a redis123 ping 2>/dev/null | grep -q PONG && echo "✅ 可访问" || echo "❌ 不可访问")"
echo "Keycloak (8180): $(curl -s http://localhost:8180 > /dev/null 2>&1 && echo "✅ 可访问" || echo "❌ 不可访问")"
echo "Nacos (8848): $(curl -s http://localhost:8848/nacos/ > /dev/null 2>&1 && echo "✅ 可访问" || echo "❌ 不可访问")"
echo ""

# 检查微服务应用
echo "🚀 微服务应用状态："
echo "认证服务 (8081): $(curl -s http://localhost:8081/actuator/health > /dev/null 2>&1 && echo "✅ 运行中" || echo "❌ 未运行")"
echo "用户服务 (8082): $(curl -s http://localhost:8082/actuator/health > /dev/null 2>&1 && echo "✅ 运行中" || echo "❌ 未运行")"
echo "网关服务 (8080): $(curl -s http://localhost:8080/actuator/health > /dev/null 2>&1 && echo "✅ 运行中" || echo "❌ 未运行")"
echo ""

# 检查进程
echo "⚙️  Java 进程："
ps aux | grep java | grep -E "(auth-service|user-service|gateway)" | awk '{print "  " $2 " " $11 " " $12 " " $13}' | head -3
echo ""

# 显示端口占用
echo "🌐 端口占用情况："
lsof -i :8080,8081,8082,3307,6379,8180,8848 2>/dev/null | grep LISTEN | awk '{print "  " $1 " " $9}' | sort | uniq
echo ""

# 显示访问地址
echo "🔗 访问地址："
echo "  网关服务: http://localhost:8080"
echo "  认证服务: http://localhost:8081"
echo "  用户服务: http://localhost:8082"
echo "  Keycloak: http://localhost:8180"
echo "  Nacos: http://localhost:8848/nacos"
echo ""

# 显示管理命令
echo "🛠️  管理命令："
echo "  启动微服务: ./start-microservices.sh"
echo "  检查状态: ./check-services-status.sh"
echo "  管理容器: cd microservice-auth-demo/keycloak-config && ./manage-services.sh"
echo ""

echo "=========================================="
