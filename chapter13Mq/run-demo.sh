#!/bin/bash

# Spring Boot ActiveMQ 示例项目启动脚本

echo "=========================================="
echo "Spring Boot ActiveMQ 示例项目"
echo "=========================================="

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "错误: 未找到Java环境，请先安装Java 8或更高版本"
    exit 1
fi

# 检查Maven环境
if ! command -v mvn &> /dev/null; then
    echo "错误: 未找到Maven环境，请先安装Maven"
    exit 1
fi

echo "Java版本:"
java -version

echo ""
echo "Maven版本:"
mvn -version

echo ""
echo "检查ActiveMQ连接..."
if ! nc -z localhost 61616 2>/dev/null; then
    echo "警告: ActiveMQ未启动或无法连接到 localhost:61616"
    echo "请先启动ActiveMQ:"
    echo "1. 下载ActiveMQ: http://activemq.apache.org/downloads"
    echo "2. 解压并启动: bin/activemq start"
    echo "3. 访问Web控制台: http://localhost:8161/admin (admin/admin)"
    echo ""
    echo "是否继续启动应用? (y/n)"
    read -r response
    if [[ ! "$response" =~ ^[Yy]$ ]]; then
        echo "退出启动"
        exit 1
    fi
else
    echo "ActiveMQ连接正常"
fi

echo ""
echo "开始编译项目..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo ""
    echo "编译成功！开始启动应用..."
    echo ""
    echo "应用将在以下地址启动:"
    echo "  主页: http://localhost:8080/chapter13Mq"
    echo "  API文档: http://localhost:8080/chapter13Mq/api/activemq/system-info"
    echo "  ActiveMQ控制台: http://localhost:8161/admin"
    echo ""
    echo "按 Ctrl+C 停止应用"
    echo ""
    
    mvn spring-boot:run
else
    echo "编译失败，请检查代码错误"
    exit 1
fi
