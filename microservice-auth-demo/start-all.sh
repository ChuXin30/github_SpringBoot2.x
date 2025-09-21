#!/bin/bash

# 微服务认证架构 - 一键启动脚本
# 使用方法: ./start-all.sh

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
NC='\033[0m' # No Color

# 打印函数
print_header() {
    echo -e "${PURPLE}╔══════════════════════════════════════════════════╗${NC}"
    echo -e "${PURPLE}║           🚀 微服务认证架构启动器                 ║${NC}"
    echo -e "${PURPLE}║      API网关 + JWT + Keycloak + Spring Security  ║${NC}"
    echo -e "${PURPLE}╚══════════════════════════════════════════════════╝${NC}"
    echo
}

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

print_wait() {
    echo -e "${YELLOW}⏳ $1${NC}"
}

# 检查前置条件
check_prerequisites() {
    print_step "检查前置条件..."
    
    # 检查Docker
    if ! command -v docker &> /dev/null; then
        print_error "请先安装Docker"
        exit 1
    fi
    
    # 检查Docker Compose
    if ! command -v docker-compose &> /dev/null; then
        print_error "请先安装Docker Compose"
        exit 1
    fi
    
    # 检查Java
    if ! command -v java &> /dev/null; then
        print_error "请先安装Java 17或更高版本"
        exit 1
    fi
    
    # 检查Maven
    if ! command -v mvn &> /dev/null; then
        print_error "请先安装Maven"
        exit 1
    fi
    
    print_success "所有前置条件检查通过"
    echo
}

# 启动基础设施
start_infrastructure() {
    print_step "启动基础设施服务 (Keycloak, MySQL, Redis, Nacos)..."
    
    cd keycloak-config
    
    # 检查是否已经在运行
    if docker-compose ps | grep -q "Up"; then
        print_info "基础设施服务已在运行，跳过启动"
    else
        docker-compose up -d
    fi
    
    cd ..
    
    print_wait "等待基础设施服务启动完成..."
    sleep 10
    
    # 等待Keycloak启动
    echo -n "等待Keycloak启动"
    timeout=120
    counter=0
    while ! curl -sf http://localhost:8180/auth/health > /dev/null 2>&1; do
        if [ $counter -ge $timeout ]; then
            print_error "Keycloak启动超时"
            exit 1
        fi
        echo -n "."
        sleep 2
        counter=$((counter + 2))
    done
    echo
    
    print_success "基础设施服务启动完成"
    echo
}

# 编译所有服务
compile_services() {
    print_step "编译所有微服务..."
    
    services=("gateway" "auth-service" "user-service")
    
    for service in "${services[@]}"; do
        print_info "编译 $service..."
        cd $service
        mvn clean compile -q -DskipTests
        cd ..
    done
    
    print_success "所有服务编译完成"
    echo
}

# 启动微服务
start_microservices() {
    print_step "启动微服务..."
    
    # 创建日志目录
    mkdir -p logs
    
    # 启动认证服务
    print_info "启动认证服务 (端口: 8081)..."
    cd auth-service
    nohup mvn spring-boot:run > ../logs/auth-service.log 2>&1 &
    AUTH_PID=$!
    cd ..
    echo $AUTH_PID > logs/auth-service.pid
    
    # 等待认证服务启动
    print_wait "等待认证服务启动..."
    sleep 15
    
    # 启动用户服务
    print_info "启动用户服务 (端口: 8082)..."
    cd user-service
    nohup mvn spring-boot:run > ../logs/user-service.log 2>&1 &
    USER_PID=$!
    cd ..
    echo $USER_PID > logs/user-service.pid
    
    # 等待用户服务启动
    print_wait "等待用户服务启动..."
    sleep 15
    
    # 启动API网关
    print_info "启动API网关 (端口: 8080)..."
    cd gateway
    nohup mvn spring-boot:run > ../logs/gateway.log 2>&1 &
    GATEWAY_PID=$!
    cd ..
    echo $GATEWAY_PID > logs/gateway.pid
    
    # 等待网关启动
    print_wait "等待API网关启动..."
    sleep 20
    
    print_success "所有微服务启动完成"
    echo
}

# 验证服务状态
verify_services() {
    print_step "验证服务状态..."
    
    services=(
        "Keycloak:http://localhost:8180/auth/health"
        "认证服务:http://localhost:8081/actuator/health"
        "用户服务:http://localhost:8082/actuator/health"
        "API网关:http://localhost:8080/actuator/health"
    )
    
    all_healthy=true
    
    for service in "${services[@]}"; do
        name=$(echo $service | cut -d: -f1)
        url=$(echo $service | cut -d: -f2-)
        
        if curl -sf $url > /dev/null 2>&1; then
            print_success "$name 运行正常"
        else
            print_error "$name 健康检查失败"
            all_healthy=false
        fi
    done
    
    if [ "$all_healthy" = true ]; then
        print_success "所有服务运行正常"
    else
        print_error "部分服务启动失败，请检查日志"
    fi
    
    echo
}

# 运行测试
run_tests() {
    print_step "运行系统测试..."
    
    # 等待一下确保所有服务完全就绪
    sleep 10
    
    if [ -f "test-api.sh" ]; then
        chmod +x test-api.sh
        if ./test-api.sh; then
            print_success "系统测试全部通过"
        else
            print_error "部分测试失败"
        fi
    else
        print_info "测试脚本不存在，跳过自动化测试"
    fi
    
    echo
}

# 显示访问信息
show_access_info() {
    print_step "系统访问信息"
    echo
    echo -e "${GREEN}🌐 系统访问地址:${NC}"
    echo -e "   • API网关入口:     ${BLUE}http://localhost:8080${NC}"
    echo -e "   • 前端演示页面:     ${BLUE}file://$(pwd)/frontend-demo/index.html${NC}"
    echo -e "   • Keycloak管理:     ${BLUE}http://localhost:8180/auth/admin${NC} (admin/admin123)"
    echo
    echo -e "${GREEN}🔧 测试账号:${NC}"
    echo -e "   • 普通用户:         ${BLUE}testuser / testpassword${NC}"
    echo -e "   • 管理员:           ${BLUE}admin / adminpassword${NC}"
    echo
    echo -e "${GREEN}📊 服务状态监控:${NC}"
    echo -e "   • API网关健康:      ${BLUE}http://localhost:8080/actuator/health${NC}"
    echo -e "   • 认证服务健康:      ${BLUE}http://localhost:8081/actuator/health${NC}"
    echo -e "   • 用户服务健康:      ${BLUE}http://localhost:8082/actuator/health${NC}"
    echo
    echo -e "${GREEN}📁 日志文件位置:${NC}"
    echo -e "   • 网关日志:         ${BLUE}logs/gateway.log${NC}"
    echo -e "   • 认证服务日志:      ${BLUE}logs/auth-service.log${NC}"
    echo -e "   • 用户服务日志:      ${BLUE}logs/user-service.log${NC}"
    echo
    echo -e "${GREEN}🛑 停止所有服务:${NC}"
    echo -e "   • 执行命令:         ${BLUE}./stop-all.sh${NC}"
    echo
}

# 主函数
main() {
    print_header
    
    # 进入项目目录
    cd "$(dirname "$0")"
    
    check_prerequisites
    start_infrastructure
    compile_services
    start_microservices
    verify_services
    run_tests
    show_access_info
    
    print_success "🎉 微服务认证架构系统启动完成！"
    echo
    print_info "💡 提示: 可以通过前端演示页面或API测试脚本来体验系统功能"
}

# 捕获Ctrl+C信号
trap 'print_error "启动被中断"; exit 1' INT

# 执行主函数
main
