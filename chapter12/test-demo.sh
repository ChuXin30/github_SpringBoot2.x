#!/bin/bash

echo "=== Spring Security 示例演示 ==="
echo

# 测试公开 API
echo "1. 测试公开 API 端点:"
curl -s http://localhost:8080/api/public | jq .
echo
echo

# 测试受保护的 API（应该重定向到登录页面）
echo "2. 测试受保护的 API 端点（未登录）:"
curl -s -w "HTTP状态码: %{http_code}\n" http://localhost:8080/api/protected
echo
echo

# 使用用户凭据登录并测试受保护的端点
echo "3. 使用普通用户凭据登录并测试受保护的 API:"
# 创建临时 cookie 文件
COOKIE_FILE=$(mktemp)

# 登录
echo "正在登录..."
curl -s -c $COOKIE_FILE -d "username=user&password=password" -X POST http://localhost:8080/login

# 使用 cookie 访问受保护的端点
echo "访问受保护的 API:"
curl -s -b $COOKIE_FILE http://localhost:8080/api/protected | jq .
echo
echo

# 测试管理员 API
echo "4. 测试管理员 API（普通用户）:"
curl -s -b $COOKIE_FILE -w "HTTP状态码: %{http_code}\n" http://localhost:8080/api/admin
echo
echo

# 使用管理员凭据登录
echo "5. 使用管理员凭据登录并测试管理员 API:"
# 重新登录为管理员
curl -s -c $COOKIE_FILE -d "username=admin&password=admin" -X POST http://localhost:8080/login

# 访问管理员 API
echo "访问管理员 API:"
curl -s -b $COOKIE_FILE http://localhost:8080/api/admin | jq .
echo
echo

# 清理临时文件
rm -f $COOKIE_FILE

echo "=== 演示完成 ==="
echo "您可以在浏览器中访问 http://localhost:8080 来查看完整的 Web 界面"
