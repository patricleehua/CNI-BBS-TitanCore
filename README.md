<div align="center">

# CNI-BBS-TitanCore

**A Modern Enterprise-Grade Forum System**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.7-brightgreen?logo=springboot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange?logo=java)](https://openjdk.org/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](CONTRIBUTING.md)

[English](#overview) | [简体中文](./README-zh.md)

</div>

---

## Overview

CNI-BBS-TitanCore is a modern, enterprise-grade forum/BBS system built with **Spring Boot 3.5.7** and **Java 17**. It features a modular architecture with custom Spring Boot starters, real-time communication via WebSocket, full-text search with Elasticsearch, and AI integration capabilities.

## Features

- **Authentication & Security**
  - JWT-based authentication with Sa-Token (72-hour session timeout)
  - Login attempt limiting (5 failed attempts = 5-minute lockout)
  - AES encryption for sensitive data
  - Sensitive word filtering for content moderation

- **Core Forum Features**
  - Posts with comments, categories, and tags
  - User follow relationships
  - Points and rewards system
  - Real-time chat via WebSocket

- **AI Integration**
  - OpenAI API support
  - Alibaba Dashscope (Tongyi Qianwen) integration
  - AI chat sessions and message management

- **Cloud Storage**
  - Aliyun OSS support
  - MinIO (S3-compatible) support
  - Tencent Cloud & Qiniu support

- **Third-party Login**
  - Google OAuth
  - Gitee OAuth

## Tech Stack

| Category | Technology |
|----------|------------|
| Framework | Spring Boot 3.5.7 |
| Runtime | Java 17+ |
| Database | MySQL 8.0+ with MyBatis-Plus |
| Cache | Redis 5.0+ with Lettuce |
| Search Engine | Elasticsearch 8.13.3 with IK Analyzer |
| Message Queue | RabbitMQ |
| Real-time | Netty WebSocket Server |
| API Docs | Knife4j (Swagger 3) |
| Authentication | Sa-Token |

## Project Structure

```
CNI-BBS-TitanCore/
├── bbs-core/                          # Main application module
│   ├── src/main/java/com/titancore/
│   │   ├── controller/                # REST API controllers
│   │   ├── service/impl/              # Business logic
│   │   ├── domain/                    # Entity, DTO, VO, Mapper
│   │   ├── websocket/                 # Netty WebSocket server
│   │   ├── task/                      # Scheduled tasks
│   │   ├── config/                    # Configuration classes
│   │   └── util/                      # Utilities
│   └── src/main/resources/
│       └── application-{profile}.yaml # Profile configs
│
├── bbs-framework/                     # Custom Spring Boot starters
│   ├── common/                        # Shared utilities & base classes
│   ├── bbs-spring-boot-starter-webmvc/
│   ├── bbs-spring-boot-starter-jackson/
│   ├── bbs-spring-boot-starter-cloud-manager/
│   ├── bbs-spring-boot-starter-rabbitmq/
│   ├── bbs-spring-boot-starter-biz-operationlog/
│   └── bbs-spring-boot-starter-ai-provider-starter/
│
├── docker/                            # Docker deployment files
│   └── docker-compose-middleware.yml
│
└── sql/                               # Database schemas
```

## Quick Start

### Prerequisites

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 5.0+
- (Optional) Elasticsearch 8.x, RabbitMQ, MinIO

### 1. Clone & Build

```bash
git clone https://github.com/patricleehua/CNI-BBS-TitanCore.git
cd CNI-BBS-TitanCore
mvn clean package -DskipTests
```

### 2. Database Setup

```bash
# Create database and import schema
mysql -u root -p -e "CREATE DATABASE titan_bbs CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -p titan_bbs < sql/all-titan-bbs-202504190057.sql
```

### 3. Configuration

Copy and edit the configuration template:

```bash
cp bbs-core/src/main/resources/application-temp.yaml \
   bbs-core/src/main/resources/application-dev.yaml
```

Configure the following in `application-dev.yaml`:

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

### 4. Run

```bash
# Development mode (default)
mvn spring-boot:run -pl bbs-core

# Or with specific profile
mvn spring-boot:run -pl bbs-core -Dspring-boot.run.profiles=dev
```

Access the application:
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui` (Basic auth: user/123456)
- Druid Monitor: `http://localhost:8080/druid/`

## Docker Deployment

### Build Image

```bash
mvn clean package
docker build -t cni-bbs-core:1.0 .
```

### Run Container

```bash
# Basic run
docker run -d -p 8080:8080 --name cni-bbs-core cni-bbs-core:1.0

# With docker profile
docker run -d -p 8080:8080 -p 9100:9100 \
  -e "SPRING_PROFILES_ACTIVE=docker" \
  --name cni-bbs-core cni-bbs-core:1.0
```

### Docker Compose (Middleware)

Deploy all middleware services with one command:

```bash
cd docker
docker-compose -f docker-compose-middleware.yml up -d
```

This starts: MySQL, Redis, Elasticsearch (with IK analyzer), RabbitMQ, and MinIO.

See [docker/README.md](docker/README.md) for detailed middleware configuration.

## Configuration Profiles

| Profile | Description |
|---------|-------------|
| `dev` | Development (default) |
| `docker` | Docker container deployment |
| `prod` | Production environment |
| `temp` | Template for new environments |

## API Documentation

Access Swagger UI at `/swagger-ui` after starting the application.

**Authentication**: Basic Auth
- Username: `user`
- Password: `123456`

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Spring Boot](https://spring.io/projects/spring-boot)
- [MyBatis-Plus](https://baomidou.com/)
- [Sa-Token](https://sa-token.cc/)
- [Knife4j](https://doc.xiaominfo.com/)

---

<div align="center">

**[⬆ Back to Top](#cni-bbs-titancore)**

</div>
