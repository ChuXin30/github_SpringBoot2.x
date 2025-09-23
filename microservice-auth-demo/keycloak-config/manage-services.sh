#!/bin/bash

# 微服务环境管理脚本

echo "=========================================="
echo "微服务环境管理脚本"
echo "=========================================="

# 设置 PATH 以包含 podman-compose
export PATH="/Users/hanggao/Library/Python/3.9/bin:$PATH"

# 切换到项目目录
cd /Users/hanggao/github_code/SpringBoot2.x/microservice-auth-demo/keycloak-config

case "$1" in
    start)
        echo "启动所有微服务..."
        podman-compose -f docker-compose-podman.yml up -d
        echo ""
        echo "服务启动完成！"
        echo "访问地址:"
        echo "  Keycloak: http://localhost:8180 (admin/admin123)"
        echo "  MySQL: localhost:3307 (root/root123)"
        echo "  Redis: localhost:6379 (密码: redis123)"
        echo "  Nacos: http://localhost:8848/nacos (nacos/nacos)"
        ;;
    stop)
        echo "停止所有微服务..."
        podman-compose -f docker-compose-podman.yml down
        echo "所有服务已停止"
        ;;
    restart)
        echo "重启所有微服务..."
        podman-compose -f docker-compose-podman.yml restart
        echo "所有服务已重启"
        ;;
    status)
        echo "微服务状态:"
        podman ps -f name=keycloak-auth-server -f name=mysql-db -f name=redis-cache
        ;;
    logs)
        echo "查看服务日志:"
        if [ -n "$2" ]; then
            podman logs $2
        else
            echo "可用服务: keycloak-auth-server, mysql-db, redis-cache"
            echo "用法: $0 logs <服务名>"
        fi
        ;;
    test)
        echo "测试服务连接..."
        echo ""
        echo "=== MySQL 测试 ==="
        podman exec mysql-db mysql -u root -proot123 -e "SHOW DATABASES;" 2>/dev/null | grep -E "(Database|microservice_db)" || echo "MySQL 连接失败"
        echo ""
        echo "=== Redis 测试 ==="
        podman exec redis-cache redis-cli -a redis123 ping 2>/dev/null || echo "Redis 连接失败"
        echo ""
        echo "=== Keycloak 测试 ==="
        curl -f http://localhost:8180/health/ready 2>/dev/null | grep -o '"status":"UP"' && echo "Keycloak 健康检查通过" || echo "Keycloak 连接失败"
        echo ""
        echo "=== Nacos 测试 ==="
        if curl -f http://localhost:8848/nacos/ 2>/dev/null | grep -i "nacos" > /dev/null; then
            echo "✅ Nacos Web 界面可访问"
            # 测试服务注册功能
            TOKEN=$(curl -s -X POST "http://localhost:8848/nacos/v1/auth/login" -d "username=nacos&password=nacos" | grep -o '"accessToken":"[^"]*"' | cut -d'"' -f4)
            if [ -n "$TOKEN" ]; then
                echo "✅ Nacos 认证成功"
                # 测试服务注册
                if curl -s -X POST "http://localhost:8848/nacos/v1/ns/instance?serviceName=health-check&ip=127.0.0.1&port=9999&accessToken=$TOKEN" | grep -q "ok"; then
                    echo "✅ Nacos 服务注册功能正常"
                else
                    echo "❌ Nacos 服务注册功能异常"
                fi
            else
                echo "❌ Nacos 认证失败"
            fi
        else
            echo "❌ Nacos Web 界面不可访问"
        fi
        ;;
    clean)
        echo "清理所有容器和数据..."
        podman-compose -f docker-compose-podman.yml down -v
        podman system prune -f
        echo "清理完成"
        ;;
    *)
        echo "用法: $0 {start|stop|restart|status|logs|test|clean}"
        echo ""
        echo "命令说明:"
        echo "  start   - 启动所有微服务"
        echo "  stop    - 停止所有微服务"
        echo "  restart - 重启所有微服务"
        echo "  status  - 查看服务状态"
        echo "  logs    - 查看服务日志 (需要指定服务名)"
        echo "  test    - 测试服务连接"
        echo "  clean   - 清理所有容器和数据"
        echo ""
        echo "服务信息:"
        echo "  Keycloak: http://localhost:8180 (admin/admin123)"
        echo "  MySQL: localhost:3307 (root/root123, app_user/app_password)"
        echo "  Redis: localhost:6379 (密码: redis123)"
        echo "  Nacos: http://localhost:8848/nacos (nacos/nacos)"
        echo "  数据库: microservice_db"
        exit 1
        ;;
esac
