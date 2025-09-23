#!/bin/bash

echo "=========================================="
echo "使用 Nginx 修复 CORS 问题"
echo "=========================================="

# 检查是否安装了 Nginx
if ! command -v nginx &> /dev/null; then
    echo "❌ Nginx 未安装，正在安装..."
    brew install nginx
fi

# 创建 Nginx 配置目录
NGINX_CONF_DIR="/opt/homebrew/etc/nginx/servers"
if [ ! -d "$NGINX_CONF_DIR" ]; then
    sudo mkdir -p "$NGINX_CONF_DIR"
fi

# 复制 CORS 修复配置
echo "📝 配置 Nginx CORS 代理..."
sudo cp /Users/hanggao/github_code/SpringBoot2.x/microservice-auth-demo/keycloak-config/nginx-cors-fix.conf "$NGINX_CONF_DIR/"

# 测试 Nginx 配置
echo "🔍 测试 Nginx 配置..."
if nginx -t; then
    echo "✅ Nginx 配置正确"
else
    echo "❌ Nginx 配置有误"
    exit 1
fi

# 启动 Nginx
echo "🚀 启动 Nginx..."
sudo nginx -s reload 2>/dev/null || sudo nginx

# 等待 Nginx 启动
sleep 2

# 测试 CORS 代理
echo "🧪 测试 CORS 代理..."
CORS_TEST=$(curl -s -I -X OPTIONS http://localhost:8849/nacos/v1/auth/login \
    -H "Origin: http://127.0.0.1:8080" \
    -H "Access-Control-Request-Method: POST" \
    -H "Access-Control-Request-Headers: Content-Type" 2>/dev/null)

if echo "$CORS_TEST" | grep -q "Access-Control-Allow-Origin"; then
    echo "✅ CORS 代理工作正常"
    echo ""
    echo "📋 CORS 头部信息："
    echo "$CORS_TEST" | grep -E "(access-control|vary)" | head -10
else
    echo "❌ CORS 代理测试失败"
    echo "响应："
    echo "$CORS_TEST"
fi

echo ""
echo "=========================================="
echo "CORS 修复完成！"
echo "=========================================="
echo ""
echo "🔗 新的访问地址："
echo "  原 Nacos 地址: http://localhost:8848/nacos"
echo "  CORS 代理地址: http://localhost:8849/nacos"
echo ""
echo "📝 使用说明："
echo "  1. 将前端应用中的 Nacos 地址改为: http://localhost:8849/nacos"
echo "  2. 或者使用环境变量: NACOS_SERVER_ADDR=localhost:8849"
echo ""
echo "🛠️  管理命令："
echo "  停止 Nginx: sudo nginx -s stop"
echo "  重启 Nginx: sudo nginx -s reload"
echo "  查看 Nginx 状态: sudo nginx -s status"
echo ""
