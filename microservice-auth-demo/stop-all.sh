#!/bin/bash

# 微服务认证架构 - 停止脚本
# 使用方法: ./stop-all.sh

set -e

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

echo -e "${BLUE}🛑 停止微服务认证架构系统...${NC}"
echo

# 进入项目目录
cd "$(dirname "$0")"

# 停止微服务
print_step "停止微服务..."

if [ -f "logs/gateway.pid" ]; then
    GATEWAY_PID=$(cat logs/gateway.pid)
    if kill -0 $GATEWAY_PID 2>/dev/null; then
        print_info "停止API网关 (PID: $GATEWAY_PID)..."
        kill $GATEWAY_PID
        rm logs/gateway.pid
    fi
else
    print_info "API网关PID文件不存在，尝试按名称查找进程..."
    pkill -f "gateway.*spring-boot:run" || true
fi

if [ -f "logs/user-service.pid" ]; then
    USER_PID=$(cat logs/user-service.pid)
    if kill -0 $USER_PID 2>/dev/null; then
        print_info "停止用户服务 (PID: $USER_PID)..."
        kill $USER_PID
        rm logs/user-service.pid
    fi
else
    print_info "用户服务PID文件不存在，尝试按名称查找进程..."
    pkill -f "user-service.*spring-boot:run" || true
fi

if [ -f "logs/auth-service.pid" ]; then
    AUTH_PID=$(cat logs/auth-service.pid)
    if kill -0 $AUTH_PID 2>/dev/null; then
        print_info "停止认证服务 (PID: $AUTH_PID)..."
        kill $AUTH_PID
        rm logs/auth-service.pid
    fi
else
    print_info "认证服务PID文件不存在，尝试按名称查找进程..."
    pkill -f "auth-service.*spring-boot:run" || true
fi

print_success "微服务已停止"
echo

# 停止基础设施服务
print_step "停止基础设施服务..."

if [ -f "keycloak-config/docker-compose.yml" ]; then
    cd keycloak-config
    if docker-compose ps | grep -q "Up"; then
        print_info "停止Docker服务..."
        docker-compose down
        print_success "Docker服务已停止"
    else
        print_info "Docker服务未运行"
    fi
    cd ..
else
    print_error "找不到docker-compose.yml文件"
fi

echo

# 清理日志文件（可选）
read -p "是否清理日志文件？ (y/n): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    print_step "清理日志文件..."
    rm -f logs/*.log
    print_success "日志文件已清理"
else
    print_info "保留日志文件"
fi

echo
print_success "🎉 系统已完全停止！"

# 显示清理信息
echo
print_info "💡 如需完全重置（包括数据库数据），可以执行："
echo -e "   ${BLUE}cd keycloak-config && docker-compose down -v${NC}"
echo
