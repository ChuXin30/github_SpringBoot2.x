#!/bin/bash

# MySQL 容器管理脚本

echo "=========================================="
echo "MySQL 容器管理脚本"
echo "=========================================="

case "$1" in
    start)
        echo "启动 MySQL 容器..."
        podman run -d --name mysql-db \
          -e MYSQL_ROOT_PASSWORD=root123 \
          -e MYSQL_DATABASE=microservice_db \
          -e MYSQL_USER=app_user \
          -e MYSQL_PASSWORD=app_password \
          -p 3307:3306 \
          mysql:8.0
        echo "MySQL 容器启动完成！"
        echo "连接信息:"
        echo "  主机: localhost"
        echo "  端口: 3307"
        echo "  用户名: root / app_user"
        echo "  密码: root123 / app_password"
        echo "  数据库: microservice_db"
        ;;
    stop)
        echo "停止 MySQL 容器..."
        podman stop mysql-db
        echo "MySQL 容器已停止"
        ;;
    restart)
        echo "重启 MySQL 容器..."
        podman restart mysql-db
        echo "MySQL 容器重启完成"
        ;;
    status)
        echo "MySQL 容器状态:"
        podman ps -f name=mysql-db
        ;;
    logs)
        echo "MySQL 容器日志:"
        podman logs mysql-db
        ;;
    connect)
        echo "连接到 MySQL..."
        podman exec -it mysql-db mysql -u root -proot123
        ;;
    test)
        echo "测试 MySQL 连接..."
        podman exec mysql-db mysql -u root -proot123 -e "SHOW DATABASES;"
        ;;
    clean)
        echo "清理 MySQL 容器和数据..."
        podman stop mysql-db 2>/dev/null
        podman rm mysql-db 2>/dev/null
        echo "MySQL 容器已清理"
        ;;
    *)
        echo "用法: $0 {start|stop|restart|status|logs|connect|test|clean}"
        echo ""
        echo "命令说明:"
        echo "  start   - 启动 MySQL 容器"
        echo "  stop    - 停止 MySQL 容器"
        echo "  restart - 重启 MySQL 容器"
        echo "  status  - 查看状态"
        echo "  logs    - 查看日志"
        echo "  connect - 连接到 MySQL 命令行"
        echo "  test    - 测试连接"
        echo "  clean   - 清理容器"
        echo ""
        echo "连接信息:"
        echo "  主机: localhost"
        echo "  端口: 3307"
        echo "  用户名: root / app_user"
        echo "  密码: root123 / app_password"
        echo "  数据库: microservice_db"
        exit 1
        ;;
esac
