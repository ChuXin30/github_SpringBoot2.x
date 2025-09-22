#!/bin/bash

# Spring Boot 多线程示例项目测试脚本

echo "=========================================="
echo "Spring Boot 多线程示例项目测试"
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
echo "开始运行测试..."

# 运行所有测试
echo ""
echo "1. 运行单元测试..."
mvn test -Dtest="*Test" -DfailIfNoTests=false

if [ $? -eq 0 ]; then
    echo ""
    echo "2. 运行集成测试..."
    mvn test -Dtest="*IntegrationTest" -DfailIfNoTests=false
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "3. 运行性能测试..."
        mvn test -Dtest="*PerformanceTest" -DfailIfNoTests=false
        
        if [ $? -eq 0 ]; then
            echo ""
            echo "=========================================="
            echo "所有测试通过！"
            echo "=========================================="
            echo ""
            echo "测试报告位置:"
            echo "- 单元测试报告: target/surefire-reports/"
            echo "- 测试覆盖率: target/site/jacoco/"
            echo ""
            echo "可以运行以下命令查看详细报告:"
            echo "mvn surefire-report:report"
            echo "mvn jacoco:report"
        else
            echo "性能测试失败"
            exit 1
        fi
    else
        echo "集成测试失败"
        exit 1
    fi
else
    echo "单元测试失败"
    exit 1
fi
