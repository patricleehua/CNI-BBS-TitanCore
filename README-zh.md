<div align="center">

# CNI-BBS-TitanCore

**现代化企业级论坛系统**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange?logo=java)](https://openjdk.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

[English](./README.md) | 简体中文

</div>

---

## 项目简介

CNI-BBS-TitanCore 是一个基于 **Spring Boot 3.5.7** 和 **Java 17** 构建的现代化企业级论坛/BBS系统。采用模块化架构设计，集成自定义 Spring Boot Starter，支持 WebSocket 实时通信、Elasticsearch 全文检索以及 AI 智能对话等功能。

## 功能特性

- **认证与安全**
  - 基于 Sa-Token 的 JWT 认证（72小时会话超时）
  - 登录失败限制（5次失败锁定5分钟）
  - AES 敏感数据加密
  - 敏感词过滤与内容审核

- **核心论坛功能**
  - 帖子发布、评论、分类与标签
  - 用户关注关系
  - 积分与奖励系统
  - WebSocket 实时聊天

- **AI 智能集成**
  - OpenAI API 支持
  - 阿里云通义千问集成
  - AI 对话会话与消息管理

- **云存储服务**
  - 阿里云 OSS 支持
  - MinIO（S3兼容）支持
  - 腾讯云 COS & 七牛云支持

- **第三方登录**
  - Google OAuth
  - Gitee OAuth

## 技术栈

| 分类 | 技术 |
|------|------|
| 核心框架 | Spring Boot 3.5.7 |
| 运行环境 | Java 17+ |
| 数据库 | MySQL 8.0+ / MyBatis-Plus |
| 缓存 | Redis 5.0+ / Lettuce |
| 搜索引擎 | Elasticsearch 8.13.3 + IK分词器 |
| 消息队列 | RabbitMQ |
| 实时通信 | Netty WebSocket Server |
| 接口文档 | Knife4j (Swagger 3) |
| 认证框架 | Sa-Token |

## 项目结构

```
CNI-BBS-TitanCore/
├── bbs-core/                          # 主应用模块
│   ├── src/main/java/com/titancore/
│   │   ├── controller/                # REST API 控制器
│   │   ├── service/impl/              # 业务逻辑实现
│   │   ├── domain/                    # 实体、DTO、VO、Mapper
│   │   ├── websocket/                 # Netty WebSocket 服务
│   │   ├── task/                      # 定时任务
│   │   ├── config/                    # 配置类
│   │   └── util/                      # 工具类
│   └── src/main/resources/
│       └── application-{profile}.yaml # 环境配置文件
│
├── bbs-framework/                     # 自定义 Spring Boot Starter
│   ├── common/                        # 公共工具与基础类
│   ├── bbs-spring-boot-starter-webmvc/
│   ├── bbs-spring-boot-starter-jackson/
│   ├── bbs-spring-boot-starter-cloud-manager/
│   ├── bbs-spring-boot-starter-rabbitmq/
│   ├── bbs-spring-boot-starter-biz-operationlog/
│   └── bbs-spring-boot-starter-ai-provider-starter/
│
├── docker/                            # Docker 部署文件
│   └── docker-compose-middleware.yml
│
└── sql/                               # 数据库脚本
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+
- （可选）Elasticsearch 8.x、RabbitMQ、MinIO

### 1. 克隆与构建

```bash
git clone https://github.com/patricleehua/CNI-BBS-TitanCore.git
cd CNI-BBS-TitanCore
mvn clean package -DskipTests
```

### 2. 数据库初始化

```bash
# 创建数据库并导入表结构
mysql -u root -p -e "CREATE DATABASE titan_bbs CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p titan_bbs < sql/all-titan-bbs-202504190057.sql
```

### 3. 配置文件

复制并编辑配置模板：

```bash
cp bbs-core/src/main/resources/application-temp.yaml \
   bbs-core/src/main/resources/application-dev.yaml
```

在 `application-dev.yaml` 中配置以下内容：

```yaml
titan:
  datasource:
    url: jdbc:mysql://localhost:3306/titan_bbs
    username: your_username
    password: your_password
  redis:
    host: localhost
    password: your_redis_password
```

### 4. 启动项目

```bash
# 开发模式启动（默认）
mvn spring-boot:run -pl bbs-core

# 指定环境启动
mvn spring-boot:run -pl bbs-core -Dspring-boot.run.profiles=dev
```

访问地址：
- API接口：`http://localhost:8080`
- 接口文档：`http://localhost:8080/swagger-ui`（Basic认证：user/123456）
- Druid监控：`http://localhost:8080/druid/`

## Docker 部署

### 构建镜像

```bash
mvn clean package
docker build -t cni-bbs-core:1.0 .
```

### 运行容器

```bash
# 基础运行
docker run -d -p 8080:8080 --name cni-bbs-core cni-bbs-core:1.0

# 使用 docker 环境配置
docker run -d -p 8080:8080 -p 9100:9100 \
  -e "SPRING_PROFILES_ACTIVE=docker" \
  --name cni-bbs-core cni-bbs-core:1.0
```

### Docker Compose（中间件一键部署）

一键部署所有中间件服务：

```bash
cd docker
docker-compose -f docker-compose-middleware.yml up -d
```

将启动：MySQL、Redis、Elasticsearch（含IK分词器）、RabbitMQ、MinIO

详细配置请参考 [docker/README.md](docker/README.md)

## 环境配置

| Profile | 描述 |
|---------|------|
| `dev` | 开发环境（默认） |
| `docker` | Docker 容器部署 |
| `prod` | 生产环境 |
| `temp` | 配置模板（新建环境时参考） |

## 接口文档

启动应用后访问 `/swagger-ui` 查看 Swagger 文档。

**认证方式**：Basic Auth
- 用户名：`user`
- 密码：`123456`

## 贡献指南

欢迎提交 Pull Request 参与贡献！

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

## 开源许可

本项目基于 MIT 协议开源，详见 [LICENSE](LICENSE) 文件。

## 致谢

- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis-Plus](https://baomidou.com/)
- [Sa-Token](https://sa-token.cc/)
- [Knife4j](https://doc.xiaominfo.com/)

---

<div align="center">

**[⬆ 返回顶部](#cni-bbs-titancore)**

</div>
