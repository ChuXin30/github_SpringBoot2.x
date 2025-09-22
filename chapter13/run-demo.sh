#!/bin/bash

# Spring Boot 多线程示例项目启动脚本

echo "=========================================="
echo "Spring Boot 多线程示例项目"
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
echo "开始编译项目..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo ""
    echo "编译成功！开始启动应用..."
    echo ""
    echo "应用将在以下地址启动:"
    echo "  主页: http://localhost:8080/chapter13"
    echo "  API文档: http://localhost:8080/chapter13/api/multithreading/system-info"
    echo ""
    echo "按 Ctrl+C 停止应用"
    echo ""
    
    mvn spring-boot:run
else
    echo "编译失败，请检查代码错误"
    exit 1
fi
