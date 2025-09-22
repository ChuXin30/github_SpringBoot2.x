#!/bin/bash

# Spring Boot ActiveMQ 示例API测试脚本

BASE_URL="http://localhost:8080/chapter13Mq/api/activemq"

echo "=========================================="
echo "Spring Boot ActiveMQ 示例API测试"
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

# 2. 发送用户消息
echo "2. 发送用户消息:"
curl -s -X POST "$BASE_URL/send/user" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "张三",
    "email": "zhangsan@example.com",
    "content": "欢迎注册我们的服务！",
    "messageType": "WELCOME"
  }' | python3 -m json.tool
echo ""

# 3. 发送订单消息
echo "3. 发送订单消息:"
curl -s -X POST "$BASE_URL/send/order" \
  -H "Content-Type: application/json" \
  -d '{
    "orderNumber": "ORD-123456",
    "userId": 1001,
    "username": "李四",
    "totalAmount": 299.99,
    "status": "PENDING",
    "items": [
      {
        "productId": 1,
        "productName": "iPhone 15",
        "quantity": 1,
        "price": 299.99
      }
    ]
  }' | python3 -m json.tool
echo ""

# 4. 发送邮件消息
echo "4. 发送邮件消息:"
curl -s -X POST "$BASE_URL/send/email" \
  -d "to=test@example.com&subject=测试邮件&content=这是一封测试邮件" | python3 -m json.tool
echo ""

# 5. 发送通知消息
echo "5. 发送通知消息:"
curl -s -X POST "$BASE_URL/send/notification" \
  -d "username=王五&title=系统通知&content=您的订单已处理完成" | python3 -m json.tool
echo ""

# 6. 发布新闻消息
echo "6. 发布新闻消息:"
curl -s -X POST "$BASE_URL/publish/news" \
  -d "title=科技新闻&content=最新科技动态&category=科技" | python3 -m json.tool
echo ""

# 7. 发布天气消息
echo "7. 发布天气消息:"
curl -s -X POST "$BASE_URL/publish/weather" \
  -d "city=北京&weather=晴天&temperature=25°C" | python3 -m json.tool
echo ""

# 8. 发布股票消息
echo "8. 发布股票消息:"
curl -s -X POST "$BASE_URL/publish/stock" \
  -d "symbol=AAPL&price=$150.00&change=+2.5%" | python3 -m json.tool
echo ""

# 9. 发送文本消息
echo "9. 发送文本消息:"
curl -s -X POST "$BASE_URL/send/text" \
  -d "destination=test.queue&message=这是一条测试文本消息" | python3 -m json.tool
echo ""

# 10. 批量发送用户消息
echo "10. 批量发送用户消息 (5条):"
curl -s -X POST "$BASE_URL/send/batch?count=5" | python3 -m json.tool
echo ""

# 11. 发送示例消息
echo "11. 发送示例消息:"
curl -s -X POST "$BASE_URL/send/sample" | python3 -m json.tool
echo ""

# 12. 获取消息统计信息
echo "12. 获取消息统计信息:"
curl -s "$BASE_URL/statistics" | python3 -m json.tool
echo ""

echo "=========================================="
echo "API测试完成！"
echo "=========================================="
echo ""
echo "提示:"
echo "- 查看应用日志可以看到消息处理的详细信息"
echo "- 访问ActiveMQ控制台查看队列和主题状态: http://localhost:8161/admin"
echo "- 使用 ./run-demo.sh 重新启动应用"
echo "- 可以通过修改参数来测试不同的场景"
