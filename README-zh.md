# CNI-BBS-TitanCore

**Welcome to CNI-BBS-TitanCore! / 欢迎使用 CNI-BBS-TitanCore！** 🚀

#### 🌏 [English](https://github.com/patricleehua/CNI-BBS-TitanCore/blob/main/README.md)

------

## 中文

### 如何运行？

#### 1. 创建数据库

- **数据库设置**：为 Titan-BBS 配置基于 MySQL 的数据库，执行 `titan-bbs.sql` 脚本以创建必要的表结构和数据。 🗄️
- **Redis 配置**：配置 Redis 数据库. ⚡
- **Elasticsearch 设置**：部署 Elasticsearch 服务. 🔍
- **邮箱服务**：配置 SMTP 服务以支持邮件发送功能. 📧
- **RabbitMQ 设置**：部署 RabbitMQ 消息队列服务. 🐇
- **存储服务**：获取阿里云 OSS 或设置 MinIO 本地存储服务. ☁️
- **短信服务**：获取阿里云 SMS 服务. 📱
- **第三方登录**：配置第三方登录功能. 🔑
- **AI 服务**：获取 AI 服务（如 OpenAI 或通义千问）. 🤖

#### 2. 环境准备

- **后端框架**：基于 Spring Boot 3.5.7. 🖥️
- **JDK 版本**：确保已安装 JDK 17 或更高版本. ☕️

##### 必需服务

- **MySQL**：版本 8.0 或更高. 🛠️
- **Redis**：版本 5.0 或更高. ⚙️

##### 配置文件

编辑 `resources` 目录下的 `application-temp.yml` 文件，配置本地数据源。

完成以上步骤后，启动后端服务，即可开始使用 CNI-BBS-TitanCore！ 🎉

------

### 如何使用 Dockerfile？

> 以下命令均在项目根目录`CNI-BBS-TitanCore`下执行

#### 打包应用

```bash
mvn clean package
```

#### 构建 Docker 镜像

```bash
docker build -t cni-bbs-core:1.0 .
```

如果你需要运行在Arm机器上，你需要使用`Docker buildx`进行构建

#### 运行 Docker 容器

- **使用默认网络**：

```bash
docker run -d -p 8080:8080 --name cni-bbs-core cni-bbs-core:1.0
docker run -d -p 8999:8080 --network 1panel-network  -e "SPRING_PROFILES_ACTIVE=docker"  --name cni-bbs-core patricleee/cni-bbs-core:1.0

docker rm -f  cni-bbs-core

docker pull  patricleee/cni-bbs-core:1.0

docker run -d -p 8999:8080 -p 9100:9100 --network 1panel-network   -e "SPRING_PROFILES_ACTIVE=docker"  --name cni-bbs-core patricleee/cni-bbs-core:1.0

docker network connect es-net cni-bbs-core


```

- **使用主机网络**：

```bash
docker run -d -p 8080:8080 --network host --name cni-bbs-core cni-bbs-core:1.0
```

#### 注意事项

- 确保配置文件已正确设置，Docker 镜像能够成功构建。 ✅
- 确保 Docker 网络允许访问 MySQL、Redis、Elasticsearch 等服务。 🔒

------

最后更新：2025.02.15
