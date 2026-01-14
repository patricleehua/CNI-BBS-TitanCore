@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

:: CNI-BBS 中间件启动脚本 (Windows)
:: 用于一键启动所有中间件服务

echo ======================================
echo CNI-BBS 中间件启动脚本
echo ======================================
echo.

:: 切换到脚本所在目录
cd /d "%~dp0"

:: 检查 .env 文件是否存在
if not exist ".env" (
    echo 📝 .env 文件不存在，从 .env.example 复制...
    copy .env.example .env >nul
    echo ✅ .env 文件已创建，请根据实际情况修改配置
    echo.
)

:: 创建数据持久化目录
echo 📁 创建数据持久化目录...
if not exist "docker\titan-bbs\elasticsearch\data" mkdir docker\titan-bbs\elasticsearch\data
if not exist "docker\titan-bbs\elasticsearch\logs" mkdir docker\titan-bbs\elasticsearch\logs
if not exist "docker\titan-bbs\elasticsearch\plugins" mkdir docker\titan-bbs\elasticsearch\plugins
if not exist "docker\titan-bbs\mysql\data" mkdir docker\titan-bbs\mysql\data
if not exist "docker\titan-bbs\mysql\logs" mkdir docker\titan-bbs\mysql\logs
if not exist "docker\titan-bbs\rabbitmq\data" mkdir docker\titan-bbs\rabbitmq\data
if not exist "docker\titan-bbs\rabbitmq\logs" mkdir docker\titan-bbs\rabbitmq\logs
if not exist "docker\titan-bbs\redis\data" mkdir docker\titan-bbs\redis\data
if not exist "docker\titan-bbs\minio\data" mkdir docker\titan-bbs\minio\data
if not exist "docker\titan-bbs\minio\config" mkdir docker\titan-bbs\minio\config

:: Windows 上 Docker Desktop 会自动处理权限映射
:: 如果遇到权限问题，请在 Docker Desktop 设置中:
:: 1. Settings - Resources - File Sharing 确保项目目录已共享
:: 2. 或者手动给予目录完全控制权限
echo ✅ 数据目录已创建
echo 💡 提示: Windows 环境下 Docker Desktop 会自动处理权限
echo.

:: 检查 Docker 是否运行
docker info >nul 2>&1
if errorlevel 1 (
    echo ❌ 错误: Docker 未运行，请先启动 Docker Desktop
    pause
    exit /b 1
)

echo ✅ Docker 运行正常
echo.

:: 显示菜单
echo 请选择启动模式：
echo 1) 启动所有中间件
echo 2) 仅启动 MySQL
echo 3) 仅启动 Redis
echo 4) 仅启动 Elasticsearch
echo 5) 仅启动 RabbitMQ
echo 6) 仅启动 MinIO
echo 7) 自定义选择
echo.
set /p choice="请输入选项 (1-6): "

if "%choice%"=="1" (
    echo.
    echo 🚀 启动所有中间件...
    docker-compose -f docker-compose-middleware.yml up -d
) else if "%choice%"=="2" (
    echo.
    echo 🚀 启动 MySQL...
    docker-compose -f docker-compose-middleware.yml up -d mysql
) else if "%choice%"=="3" (
    echo.
    echo 🚀 启动 Redis...
    docker-compose -f docker-compose-middleware.yml up -d redis
) else if "%choice%"=="4" (
    echo.
    echo 🚀 启动 Elasticsearch...
    docker-compose -f docker-compose-middleware.yml up -d elasticsearch
) else if "%choice%"=="5" (
    echo.
    echo 🚀 启动 RabbitMQ...
    docker-compose -f docker-compose-middleware.yml up -d rabbitmq
) else if "%choice%"=="6" (
    echo.
    echo 🚀 启动 MinIO...
    docker-compose -f docker-compose-middleware.yml up -d minio
) else if "%choice%"=="7" (
    echo.
    echo 可选服务：
    echo   elasticsearch - Elasticsearch 搜索引擎
    echo   mysql         - MySQL 数据库
    echo   redis         - Redis 缓存
    echo   rabbitmq      - RabbitMQ 消息队列
    echo   minio         - MinIO 对象存储
    echo.
    set /p services="请输入要启动的服务（用空格分隔）: "
    echo.
    echo 🚀 启动选定的中间件...
    docker-compose -f docker-compose-middleware.yml up -d !services!
) else (
    echo ❌ 无效选项
    pause
    exit /b 1
)

echo.
echo ⏳ 等待服务启动...
timeout /t 5 /nobreak >nul

echo.
echo ======================================
echo 📊 服务状态
echo ======================================
docker-compose -f docker-compose-middleware.yml ps
