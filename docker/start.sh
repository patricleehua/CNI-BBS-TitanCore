#!/bin/bash

# CNI-BBS 中间件启动脚本
# 用于一键启动所有中间件服务

set -e

echo "======================================"
echo "CNI-BBS 中间件启动脚本"
echo "======================================"
echo ""

# 切换到 docker 目录
cd "$(dirname "$0")"

# 检查 .env 文件是否存在
if [ ! -f ".env" ]; then
    echo "📝 .env 文件不存在，从 .env.example 复制..."
    cp .env.example .env
    echo "✅ .env 文件已创建，请根据实际情况修改配置"
    echo ""
fi

# 创建数据持久化目录
echo "📁 创建数据持久化目录..."
mkdir -p docker/titan-bbs/elasticsearch/data
mkdir -p docker/titan-bbs/elasticsearch/logs
mkdir -p docker/titan-bbs/elasticsearch/plugins
mkdir -p docker/titan-bbs/mysql/data
mkdir -p docker/titan-bbs/mysql/logs
mkdir -p docker/titan-bbs/rabbitmq/data
mkdir -p docker/titan-bbs/rabbitmq/logs
mkdir -p docker/titan-bbs/redis/data
mkdir -p docker/titan-bbs/minio/data
mkdir -p docker/titan-bbs/minio/config

# 设置目录权限（针对容器内的特定UID/GID）
if [ "$(uname)" != "Darwin" ]; then
    echo "🔐 设置目录权限..."
    # Elasticsearch 需要 UID 1000
    sudo chown -R 1000:1000 docker/titan-bbs/elasticsearch 2>/dev/null || chmod -R 777 docker/titan-bbs/elasticsearch
    # RabbitMQ 需要 UID 999
    sudo chown -R 999:999 docker/titan-bbs/rabbitmq 2>/dev/null || chmod -R 777 docker/titan-bbs/rabbitmq
    # MySQL 需要 UID 999
    sudo chown -R 999:999 docker/titan-bbs/mysql 2>/dev/null || chmod -R 777 docker/titan-bbs/mysql
    # Redis 需要 UID 999
    sudo chown -R 999:999 docker/titan-bbs/redis 2>/dev/null || chmod -R 777 docker/titan-bbs/redis
else
    # macOS/Docker Desktop 会自动处理权限
    chmod -R 755 docker/titan-bbs
fi

echo "✅ 数据目录已创建并设置权限"
echo ""

# 检查 Docker 是否运行
if ! docker info > /dev/null 2>&1; then
    echo "❌ 错误: Docker 未运行，请先启动 Docker"
    exit 1
fi

echo "✅ Docker 运行正常"
echo ""

# 显示菜单
echo "请选择启动模式："
echo "1) 启动所有中间件"
echo "2) 仅启动 MySQL"
echo "3) 仅启动 Redis"
echo "4) 仅启动 Elasticsearch"
echo "5) 仅启动 RabbitMQ"
echo "6) 仅启动 MinIO"
echo "7) 自定义选择"
echo ""
read -p "请输入选项 (1-6): " choice

case $choice in
    1)
        echo ""
        echo "🚀 启动所有中间件..."
        docker-compose -f docker-compose-middleware.yml up -d
        ;;
    2)
        echo ""
        echo "🚀 启动 MySQL..."
        docker-compose -f docker-compose-middleware.yml up -d mysql
        ;;
    3)
        echo ""
        echo "启动 Redis..."
        docker-compose -f docker-compose-middleware.yml up -d redis
        ;;
    4)
        echo ""
        echo "🚀 启动 Elasticsearch..."
        docker-compose -f docker-compose-middleware.yml up -d elasticsearch
        ;;
    5)
        echo ""
        echo "🚀 启动 RabbitMQ..."
        docker-compose -f docker-compose-middleware.yml up -d rabbitmq
        ;;
    6)
        echo ""
        echo "🚀 启动 MinIO..."
        docker-compose -f docker-compose-middleware.yml up -d minio
        ;;
    7)
        echo ""
        echo "可选服务："
        echo "  elasticsearch - Elasticsearch 搜索引擎"
        echo "  mysql         - MySQL 数据库"
        echo "  redis         - Redis 缓存"
        echo "  rabbitmq      - RabbitMQ 消息队列"
        echo "  minio         - MinIO 对象存储"
        echo ""
        read -p "请输入要启动的服务（用空格分隔）: " services
        echo ""
        echo "🚀 启动选定的中间件..."
        docker-compose -f docker-compose-middleware.yml up -d $services
        ;;
    *)
        echo "❌ 无效选项"
        exit 1
        ;;
esac

echo ""
echo "⏳ 等待服务启动..."
sleep 5

echo ""
echo "======================================"
echo "📊 服务状态"
echo "======================================"
docker-compose -f docker-compose-middleware.yml ps

