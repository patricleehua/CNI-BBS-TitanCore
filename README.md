# CNI-BBS-TitanCore

**Welcome to CNI-BBS-TitanCore! / 欢迎使用 CNI-BBS-TitanCore！** 🚀

#### 🌏 [Chinese](https://github.com/patricleehua/CNI-BBS-TitanCore/blob/main/README-zh.md)

### How to Run?

#### 1. Create the Database

- **Database setup**: Configure the MySQL-based database for Titan-BBS and run the `titan-bbs.sql` script to create the necessary schema and tables. 🗄️
- **Redis setup**: Configure the Redis database. ⚡
- **Elasticsearch setup**: Deploy Elasticsearch services. 🔍
- **Email services**: Obtain an SMTP service for email functionality. 📧
- **RabbitMQ setup**: Deploy RabbitMQ message queue services. 🐇
- **Storage services**: Obtain Alibaba Cloud OSS or set up MinIO for local storage. ☁️
- **SMS services**: Obtain Alibaba Cloud SMS services. 📱
- **Third-party login**: Set up third-party login functionality. 🔑
- **AI services**: Obtain AI services (e.g., OpenAI or Tongyi Qianwen). 🤖

#### 2. Environment Preparation

- **Backend Framework**: This system is based on Spring Boot 3.5.7. 🖥️
- **JDK Version**: Ensure you have JDK 17 or higher installed. ☕️

##### Required Services

- **MySQL**: Version 8.0 or higher. 🛠️
- **Redis**: Version 5.0 or higher. ⚙️

##### Configuration

Edit the `application-temp.yml` file in the `resources` directory to configure your local data source.

Once these steps are completed, start the backend and enjoy using CNI-BBS-TitanCore! 🎉

------

### How to Use the Dockerfile?

> The following commands are all executed in the project root directory `CNI-BBS-TitanCore`

#### Package the Application

```bash
mvn clean package
```

#### Build the Docker Image

```bash
docker build -t cni-bbs-core:1.0 .
```

If you need to run on an Arm machine, you need to build using `Docker buildx`

#### Run the Docker Container

- **Using default network**:

```bash
docker run -d -p 8080:8080 --name cni-bbs-core cni-bbs-core:1.0
```

- **Using host network**:

```bash
docker run -d -p 8080:8080 --network host --name cni-bbs-core cni-bbs-core:1.0
```

#### Notices

- Ensure your configuration files are properly set up, and the Docker image builds successfully. ✅
- Ensure your Docker network allows access to MySQL, Redis, Elasticsearch, and other services (e.g., AI services). 🔒

------

Last Update: 2025.02.15
