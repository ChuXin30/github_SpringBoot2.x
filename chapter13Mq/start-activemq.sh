#!/bin/bash

# ActiveMQ Docker 管理脚本

echo "=========================================="
echo "ActiveMQ Docker 管理脚本"
echo "=========================================="

# 检查 Docker 是否运行
if ! docker info > /dev/null 2>&1; then
    echo "错误: Docker 未运行，请先启动 Docker"
    exit 1
fi

# 检查是否存在 docker-compose.yml
if [ ! -f "docker-compose.yml" ]; then
    echo "错误: 未找到 docker-compose.yml 文件"
    exit 1
fi

case "$1" in
    start)
        echo "启动 ActiveMQ..."
        docker-compose up -d
        echo ""
        echo "ActiveMQ 启动完成！"
        echo "Web 控制台: http://localhost:8161"
        echo "用户名/密码: admin/admin"
        echo "JMS 连接地址: tcp://localhost:61616"
        ;;
    stop)
        echo "停止 ActiveMQ..."
        docker-compose down
        echo "ActiveMQ 已停止"
        ;;
    restart)
        echo "重启 ActiveMQ..."
        docker-compose restart
        echo "ActiveMQ 重启完成"
        ;;
    status)
        echo "ActiveMQ 状态:"
        docker-compose ps
        ;;
    logs)
        echo "ActiveMQ 日志:"
        docker-compose logs -f activemq
        ;;
    clean)
        echo "清理 ActiveMQ 数据..."
        docker-compose down -v
        echo "ActiveMQ 数据已清理"
        ;;
    *)
        echo "用法: $0 {start|stop|restart|status|logs|clean}"
        echo ""
        echo "命令说明:"
        echo "  start   - 启动 ActiveMQ"
        echo "  stop    - 停止 ActiveMQ"
        echo "  restart - 重启 ActiveMQ"
        echo "  status  - 查看状态"
        echo "  logs    - 查看日志"
        echo "  clean   - 清理数据"
        echo ""
        echo "访问地址:"
        echo "  Web 控制台: http://localhost:8161"
        echo "  JMS 连接: tcp://localhost:61616"
        echo "  用户名/密码: admin/admin"
        exit 1
        ;;
esac
