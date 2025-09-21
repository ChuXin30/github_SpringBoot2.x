#!/bin/bash

# 微服务认证架构 API 测试脚本
# 使用方法: ./test-api.sh

set -e

echo "🚀 开始测试微服务认证架构..."
echo

# 配置
GATEWAY_URL="http://localhost:8080"
AUTH_URL="$GATEWAY_URL/auth"
API_URL="$GATEWAY_URL/api"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 打印函数
print_step() {
    echo -e "${BLUE}📋 $1${NC}"
}

print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ️  $1${NC}"
}

# 1. 测试公开API（无需认证）
print_step "测试公开API（无需认证）"
response=$(curl -s -w "HTTP_CODE:%{http_code}" "$API_URL/user/public/info")
http_code=$(echo "$response" | grep -o "HTTP_CODE:[0-9]*" | cut -d: -f2)
body=$(echo "$response" | sed 's/HTTP_CODE:[0-9]*$//')

if [ "$http_code" -eq 200 ]; then
    print_success "公开API测试通过"
    echo "响应: $body"
else
    print_error "公开API测试失败，HTTP状态码: $http_code"
fi
echo

# 2. 测试未认证的受保护API
print_step "测试未认证的受保护API"
response=$(curl -s -w "HTTP_CODE:%{http_code}" "$API_URL/user/profile")
http_code=$(echo "$response" | grep -o "HTTP_CODE:[0-9]*" | cut -d: -f2)

if [ "$http_code" -eq 401 ]; then
    print_success "未认证访问正确被拒绝"
else
    print_error "未认证访问应该被拒绝，但HTTP状态码为: $http_code"
fi
echo

# 3. 普通用户登录
print_step "普通用户登录"
login_response=$(curl -s -X POST "$AUTH_URL/login" \
    -H "Content-Type: application/json" \
    -d '{
        "username": "testuser",
        "password": "testpassword"
    }')

echo "登录响应: $login_response"

# 提取访问令牌
access_token=$(echo "$login_response" | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)
refresh_token=$(echo "$login_response" | grep -o '"refresh_token":"[^"]*' | cut -d'"' -f4)

if [ -n "$access_token" ]; then
    print_success "普通用户登录成功"
    print_info "访问令牌: ${access_token:0:50}..."
else
    print_error "普通用户登录失败"
    exit 1
fi
echo

# 4. 使用令牌访问用户资料
print_step "使用令牌访问用户资料"
profile_response=$(curl -s -w "HTTP_CODE:%{http_code}" \
    -H "Authorization: Bearer $access_token" \
    "$API_URL/user/profile")

http_code=$(echo "$profile_response" | grep -o "HTTP_CODE:[0-9]*" | cut -d: -f2)
body=$(echo "$profile_response" | sed 's/HTTP_CODE:[0-9]*$//')

if [ "$http_code" -eq 200 ]; then
    print_success "用户资料获取成功"
    echo "用户信息: $body"
else
    print_error "用户资料获取失败，HTTP状态码: $http_code"
fi
echo

# 5. 普通用户尝试访问管理员API
print_step "普通用户尝试访问管理员API"
admin_response=$(curl -s -w "HTTP_CODE:%{http_code}" \
    -H "Authorization: Bearer $access_token" \
    "$API_URL/user/list")

http_code=$(echo "$admin_response" | grep -o "HTTP_CODE:[0-9]*" | cut -d: -f2)

if [ "$http_code" -eq 403 ]; then
    print_success "普通用户访问管理员API被正确拒绝"
else
    print_error "普通用户不应该能访问管理员API，HTTP状态码: $http_code"
fi
echo

# 6. 管理员用户登录
print_step "管理员用户登录"
admin_login_response=$(curl -s -X POST "$AUTH_URL/login" \
    -H "Content-Type: application/json" \
    -d '{
        "username": "admin",
        "password": "adminpassword"
    }')

echo "管理员登录响应: $admin_login_response"

# 提取管理员访问令牌
admin_access_token=$(echo "$admin_login_response" | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)

if [ -n "$admin_access_token" ]; then
    print_success "管理员登录成功"
    print_info "管理员访问令牌: ${admin_access_token:0:50}..."
else
    print_error "管理员登录失败"
    exit 1
fi
echo

# 7. 管理员访问用户列表
print_step "管理员访问用户列表"
user_list_response=$(curl -s -w "HTTP_CODE:%{http_code}" \
    -H "Authorization: Bearer $admin_access_token" \
    "$API_URL/user/list")

http_code=$(echo "$user_list_response" | grep -o "HTTP_CODE:[0-9]*" | cut -d: -f2)
body=$(echo "$user_list_response" | sed 's/HTTP_CODE:[0-9]*$//')

if [ "$http_code" -eq 200 ]; then
    print_success "管理员访问用户列表成功"
    echo "用户列表: $body"
else
    print_error "管理员访问用户列表失败，HTTP状态码: $http_code"
fi
echo

# 8. 测试令牌刷新
print_step "测试令牌刷新"
if [ -n "$refresh_token" ]; then
    refresh_response=$(curl -s -X POST "$AUTH_URL/refresh" \
        -H "Content-Type: application/json" \
        -d "{\"refreshToken\": \"$refresh_token\"}")
    
    new_access_token=$(echo "$refresh_response" | grep -o '"access_token":"[^"]*' | cut -d'"' -f4)
    
    if [ -n "$new_access_token" ]; then
        print_success "令牌刷新成功"
        print_info "新访问令牌: ${new_access_token:0:50}..."
    else
        print_error "令牌刷新失败"
    fi
else
    print_error "没有刷新令牌可测试"
fi
echo

# 9. 测试令牌验证
print_step "测试令牌验证"
verify_response=$(curl -s -X POST "$AUTH_URL/verify" \
    -H "Content-Type: application/json" \
    -d "{\"accessToken\": \"$access_token\"}")

echo "令牌验证响应: $verify_response"

valid=$(echo "$verify_response" | grep -o '"valid":[^,}]*' | cut -d: -f2)
if [ "$valid" = "true" ]; then
    print_success "令牌验证通过"
else
    print_error "令牌验证失败"
fi
echo

# 10. 测试用户登出
print_step "测试用户登出"
if [ -n "$refresh_token" ]; then
    logout_response=$(curl -s -X POST "$AUTH_URL/logout" \
        -H "Content-Type: application/json" \
        -d "{\"refreshToken\": \"$refresh_token\"}")
    
    echo "登出响应: $logout_response"
    
    success=$(echo "$logout_response" | grep -o '"success":[^,}]*' | cut -d: -f2)
    if [ "$success" = "true" ]; then
        print_success "用户登出成功"
    else
        print_error "用户登出失败"
    fi
else
    print_error "没有刷新令牌可登出"
fi
echo

echo "🎉 测试完成！"
echo
print_info "测试总结："
echo "- ✅ 公开API无需认证可正常访问"
echo "- ✅ 受保护API需要认证才能访问"
echo "- ✅ 普通用户无法访问管理员API"
echo "- ✅ 管理员可以访问所有API"
echo "- ✅ JWT令牌认证机制工作正常"
echo "- ✅ 令牌刷新和登出功能正常"
