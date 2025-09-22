#!/bin/bash

# Spring Boot 多线程示例API测试脚本

BASE_URL="http://localhost:8080/chapter13/api/multithreading"

echo "=========================================="
echo "Spring Boot 多线程示例API测试"
echo "=========================================="

# 检查服务是否启动
echo "检查服务状态..."
if ! curl -s "$BASE_URL/system-info" > /dev/null; then
    echo "错误: 服务未启动，请先运行 ./run-demo.sh 启动应用"
    exit 1
fi

echo "服务已启动，开始API测试..."
echo ""

# 1. 获取系统信息
echo "1. 获取系统信息:"
curl -s "$BASE_URL/system-info" | python3 -m json.tool
echo ""

# 2. 基本异步任务
echo "2. 基本异步任务:"
curl -s -X POST "$BASE_URL/basic-async?taskName=测试任务1" | python3 -m json.tool
echo ""

# 3. 带返回值的异步任务
echo "3. 带返回值的异步任务:"
curl -s -X POST "$BASE_URL/async-with-result?taskName=带返回值任务" | python3 -m json.tool
echo ""

# 4. I/O密集型任务
echo "4. I/O密集型任务:"
curl -s -X POST "$BASE_URL/io-intensive?taskName=I/O密集型任务" | python3 -m json.tool
echo ""

# 5. CPU密集型任务
echo "5. CPU密集型任务:"
curl -s -X POST "$BASE_URL/cpu-intensive?taskName=CPU密集型任务" | python3 -m json.tool
echo ""

# 6. 并发网络请求
echo "6. 并发网络请求 (5个请求):"
curl -s -X POST "$BASE_URL/concurrent-requests?requestCount=5" | python3 -m json.tool
echo ""

# 7. 并发数据库操作
echo "7. 并发数据库操作 (3个操作):"
curl -s -X POST "$BASE_URL/concurrent-db-operations?operationCount=3" | python3 -m json.tool
echo ""

# 8. 批量异步任务
echo "8. 批量异步任务 (10个任务):"
curl -s -X POST "$BASE_URL/batch-tasks?taskCount=10" | python3 -m json.tool
echo ""

# 9. 混合任务类型
echo "9. 混合任务类型:"
curl -s -X POST "$BASE_URL/mixed-tasks" | python3 -m json.tool
echo ""

echo "=========================================="
echo "API测试完成！"
echo "=========================================="
echo ""
echo "提示:"
echo "- 查看应用日志可以看到多线程执行的详细信息"
echo "- 可以通过修改参数来测试不同的场景"
echo "- 使用 ./run-demo.sh 重新启动应用"
