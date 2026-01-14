#!/bin/bash

# CNI-BBS 中间件停止脚本
# 用于停止中间件服务

set -e

echo "======================================"
echo "CNI-BBS 中间件停止脚本"
echo "======================================"
echo ""

cd "$(dirname "$0")"

# 显示菜单
echo "请选择停止模式："
echo "1) 停止所有中间件"
echo "2) 停止所有中间件并删除容器（保留数据）"
echo "3) 停止所有中间件并删除容器和数据（危险操作）"
echo "4) 停止指定服务"
echo ""
read -p "请输入选项 (1-4): " choice

case $choice in
    1)
        echo ""
        echo "🛑 停止所有中间件..."
        docker-compose -f docker-compose-middleware.yml stop
        echo "✅ 已停止所有服务"
        ;;
    2)
        echo ""
        echo "⚠️  警告: 将删除所有容器，但保留数据卷"
        read -p "确认继续？(yes/no): " confirm
        if [ "$confirm" = "yes" ]; then
            docker-compose -f docker-compose-middleware.yml down
            echo "✅ 已停止并删除容器（数据已保留）"
        else
            echo "❌ 操作已取消"
        fi
        ;;
    3)
        echo ""
        echo "🚨 危险操作: 将删除所有容器和数据卷！"
        echo "⚠️  这将永久删除所有数据，包括："
        echo "   - MySQL 数据库"
        echo "   - Redis 缓存"
        echo "   - Elasticsearch 索引"
        echo "   - RabbitMQ 队列"
        echo "   - MinIO 文件"
        echo ""
        read -p "请输入 'DELETE ALL DATA' 确认删除: " confirm
        if [ "$confirm" = "DELETE ALL DATA" ]; then
            docker-compose -f docker-compose-middleware.yml down -v
            echo "✅ 已停止并删除所有容器和数据"
        else
            echo "❌ 操作已取消"
        fi
        ;;
    4)
        echo ""
        echo "可选服务："
        echo "  elasticsearch - Elasticsearch 搜索引擎"
        echo "  mysql         - MySQL 数据库"
        echo "  redis         - Redis 缓存"
        echo "  rabbitmq      - RabbitMQ 消息队列"
        echo "  minio         - MinIO 对象存储"
        echo ""
        read -p "请输入要停止的服务（用空格分隔）: " services
        echo ""
        echo "🛑 停止选定的中间件..."
        docker-compose -f docker-compose-middleware.yml stop $services
        echo "✅ 已停止选定的服务"
        ;;
    *)
        echo "❌ 无效选项"
        exit 1
        ;;
esac

echo ""
echo "======================================"
echo "当前状态"
echo "======================================"
docker-compose -f docker-compose-middleware.yml ps

echo ""
