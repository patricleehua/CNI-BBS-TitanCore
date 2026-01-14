# MySQL 8.0 数据库配置说明

MySQL 是 CNI-BBS 项目的主数据库，存储用户、帖子、评论等核心业务数据。

## 配置概览

- **版本**: MySQL 8.0
- **默认凭证**:
  - Root 密码: `titan_bbs_root_2025`
  - 应用用户: `titan_bbs`
  - 应用密码: `titan_bbs_2025`
- **数据库名称**: `titan_bbs`
- **字符集**: UTF8MB4（支持 emoji 等特殊字符）
- **时区**: Asia/Shanghai (+08:00)
- **端口**: 3306

## 功能特性

- ✅ UTF8MB4 字符集（支持 emoji）
- ✅ 时区设置为东八区
- ✅ 慢查询日志（记录超过 2 秒的查询）
- ✅ 二进制日志（用于数据恢复和主从复制）
- ✅ 自动创建数据库和用户
- ✅ InnoDB 引擎优化
- ✅ 连接数优化（最大 500 连接）

## 快速开始

### 启动 MySQL

```bash
cd docker
docker-compose -f docker-compose-middleware.yml up -d mysql
```

### 验证服务状态

```bash
# 查看容器状态
docker ps | grep titan-bbs-mysql

# 查看健康状态
docker inspect --format='{{.State.Health.Status}}' titan-bbs-mysql

# 查看日志
docker logs titan-bbs-mysql
```

### 连接数据库

```bash
# 使用 root 用户连接
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025

# 使用应用用户连接
docker exec -it titan-bbs-mysql mysql -utitan_bbs -ptitan_bbs_2025 titan_bbs

# 从宿主机连接（需要安装 MySQL 客户端）
mysql -h127.0.0.1 -P3306 -utitan_bbs -ptitan_bbs_2025 titan_bbs
```

## 数据初始化

### 方式一：使用最新的 SQL 文件（推荐）

```bash
# 查看可用的 SQL 文件
ls ../sql/

# 导入最新的 SQL 文件（2025-04-19）
docker exec -i titan-bbs-mysql mysql -utitan_bbs -ptitan_bbs_2025 titan_bbs < ../sql/all-titan-bbs-202504190057.sql

# 验证导入
docker exec -it titan-bbs-mysql mysql -utitan_bbs -ptitan_bbs_2025 titan_bbs -e "SHOW TABLES;"
```

### 方式二：手动导入

```bash
# 复制 SQL 文件到容器
docker cp ../sql/all-titan-bbs-202504190057.sql titan-bbs-mysql:/tmp/

# 进入容器执行导入
docker exec -it titan-bbs-mysql /bin/bash
mysql -utitan_bbs -ptitan_bbs_2025 titan_bbs < /tmp/all-titan-bbs-202504190057.sql

# 或者使用 source 命令
docker exec -it titan-bbs-mysql mysql -utitan_bbs -ptitan_bbs_2025 titan_bbs
> source /tmp/all-titan-bbs-202504190057.sql;
```

### 方式三：通过 Navicat/DBeaver 等工具

1. 连接信息：
   - 主机: localhost 或 127.0.0.1
   - 端口: 3306
   - 用户名: titan_bbs
   - 密码: titan_bbs_2025
   - 数据库: titan_bbs

2. 导入 SQL 文件：
   - 选择 "运行 SQL 文件"
   - 选择 `sql/all-titan-bbs-202504190057.sql`
   - 执行导入

## 在 Java 项目中使用

### 配置文件设置

在 `application-docker.yaml` 中配置（Docker 环境）：

```yaml
titan:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    host: titan-bbs-mysql
    port: 3306
    database: titan_bbs
    username: titan_bbs
    password: titan_bbs_2025
```

在 `application-dev.yaml` 中配置（本地开发）：

```yaml
titan:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    host: localhost
    port: 3306
    database: titan_bbs
    username: titan_bbs
    password: titan_bbs_2025
```

## 数据库管理

### 查看数据库信息

```bash
# 查看所有数据库
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "SHOW DATABASES;"

# 查看当前数据库所有表
docker exec -it titan-bbs-mysql mysql -utitan_bbs -ptitan_bbs_2025 titan_bbs -e "SHOW TABLES;"

# 查看表结构
docker exec -it titan-bbs-mysql mysql -utitan_bbs -ptitan_bbs_2025 titan_bbs -e "DESC user;"

# 查看表记录数
docker exec -it titan-bbs-mysql mysql -utitan_bbs -ptitan_bbs_2025 titan_bbs -e "SELECT COUNT(*) FROM user;"

# 查看数据库大小
docker exec -it titan-bbs-mysql mysql -utitan_bbs -ptitan_bbs_2025 -e "
SELECT
  table_schema AS 'Database',
  ROUND(SUM(data_length + index_length) / 1024 / 1024, 2) AS 'Size (MB)'
FROM information_schema.tables
WHERE table_schema = 'titan_bbs'
GROUP BY table_schema;
"
```

### 查看表信息

```bash
# 查看所有表及其大小
docker exec -it titan-bbs-mysql mysql -utitan_bbs -ptitan_bbs_2025 -e "
SELECT
  table_name AS 'Table',
  table_rows AS 'Rows',
  ROUND(data_length / 1024 / 1024, 2) AS 'Data (MB)',
  ROUND(index_length / 1024 / 1024, 2) AS 'Index (MB)',
  ROUND((data_length + index_length) / 1024 / 1024, 2) AS 'Total (MB)'
FROM information_schema.tables
WHERE table_schema = 'titan_bbs'
ORDER BY (data_length + index_length) DESC;
"
```

### 用户和权限管理

```bash
# 查看所有用户
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "SELECT user, host FROM mysql.user;"

# 查看用户权限
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "SHOW GRANTS FOR 'titan_bbs'@'%';"

# 创建新用户（只读权限）
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "
CREATE USER 'readonly'@'%' IDENTIFIED BY 'readonly_password';
GRANT SELECT ON titan_bbs.* TO 'readonly'@'%';
FLUSH PRIVILEGES;
"

# 修改用户密码
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "
ALTER USER 'titan_bbs'@'%' IDENTIFIED BY 'new_password';
FLUSH PRIVILEGES;
"

# 删除用户
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "DROP USER 'readonly'@'%';"
```

## 数据备份与恢复

### 完整备份

```bash
# 备份整个数据库（包含结构和数据）
docker exec titan-bbs-mysql mysqldump -utitan_bbs -ptitan_bbs_2025 \
  --single-transaction \
  --routines \
  --triggers \
  --events \
  titan_bbs > backup_titan_bbs_$(date +%Y%m%d_%H%M%S).sql

# 仅备份结构（不含数据）
docker exec titan-bbs-mysql mysqldump -utitan_bbs -ptitan_bbs_2025 \
  --no-data \
  titan_bbs > backup_titan_bbs_structure_$(date +%Y%m%d).sql

# 仅备份数据（不含结构）
docker exec titan-bbs-mysql mysqldump -utitan_bbs -ptitan_bbs_2025 \
  --no-create-info \
  titan_bbs > backup_titan_bbs_data_$(date +%Y%m%d).sql
```

### 备份单个表

```bash
# 备份单个表
docker exec titan-bbs-mysql mysqldump -utitan_bbs -ptitan_bbs_2025 \
  titan_bbs user > backup_user_table_$(date +%Y%m%d).sql

# 备份多个表
docker exec titan-bbs-mysql mysqldump -utitan_bbs -ptitan_bbs_2025 \
  titan_bbs user post comment > backup_core_tables_$(date +%Y%m%d).sql
```

### 恢复数据

```bash
# 恢复整个数据库
docker exec -i titan-bbs-mysql mysql -utitan_bbs -ptitan_bbs_2025 titan_bbs < backup_titan_bbs_20250114.sql

# 恢复单个表（需要先删除原表或使用 --force）
docker exec -i titan-bbs-mysql mysql -utitan_bbs -ptitan_bbs_2025 titan_bbs < backup_user_table_20250114.sql
```

### 定时自动备份

创建备份脚本 `mysql-backup.sh`:

```bash
#!/bin/bash
BACKUP_DIR="/path/to/backup"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="${BACKUP_DIR}/titan_bbs_${TIMESTAMP}.sql"

# 创建备份
docker exec titan-bbs-mysql mysqldump -utitan_bbs -ptitan_bbs_2025 \
  --single-transaction \
  --routines \
  --triggers \
  --events \
  titan_bbs > ${BACKUP_FILE}

# 压缩备份
gzip ${BACKUP_FILE}

# 删除 7 天前的备份
find ${BACKUP_DIR} -name "titan_bbs_*.sql.gz" -mtime +7 -delete

echo "Backup completed: ${BACKUP_FILE}.gz"
```

添加到 crontab：
```bash
# 每天凌晨 2 点自动备份
0 2 * * * /path/to/mysql-backup.sh
```

## 性能优化

### 查看慢查询日志

```bash
# 进入容器查看慢查询日志
docker exec -it titan-bbs-mysql cat /var/log/mysql/slow.log

# 使用 mysqldumpslow 分析慢查询
docker exec -it titan-bbs-mysql mysqldumpslow /var/log/mysql/slow.log

# 查询慢查询统计
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "
SELECT COUNT(*) AS slow_queries FROM mysql.slow_log;
"
```

### 查看当前连接

```bash
# 查看所有连接
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "SHOW PROCESSLIST;"

# 查看连接数统计
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "
SHOW STATUS LIKE '%Threads%';
SHOW STATUS LIKE '%Connections%';
"

# 杀死某个连接（替换 <id>）
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "KILL <id>;"
```

### 查看 InnoDB 状态

```bash
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "SHOW ENGINE INNODB STATUS\G"
```

### 分析表和优化

```bash
# 分析表
docker exec -it titan-bbs-mysql mysql -utitan_bbs -ptitan_bbs_2025 titan_bbs -e "ANALYZE TABLE user;"

# 优化表
docker exec -it titan-bbs-mysql mysql -utitan_bbs -ptitan_bbs_2025 titan_bbs -e "OPTIMIZE TABLE user;"

# 检查表
docker exec -it titan-bbs-mysql mysql -utitan_bbs -ptitan_bbs_2025 titan_bbs -e "CHECK TABLE user;"

# 修复表
docker exec -it titan-bbs-mysql mysql -utitan_bbs -ptitan_bbs_2025 titan_bbs -e "REPAIR TABLE user;"
```

### 查看索引使用情况

```bash
# 查看表的索引
docker exec -it titan-bbs-mysql mysql -utitan_bbs -ptitan_bbs_2025 titan_bbs -e "SHOW INDEX FROM user;"

# 查看未使用的索引
docker exec -it titan-bbs-mysql mysql -utitan_bbs -ptitan_bbs_2025 -e "
SELECT
  object_schema,
  object_name,
  index_name
FROM performance_schema.table_io_waits_summary_by_index_usage
WHERE index_name IS NOT NULL
  AND count_star = 0
  AND object_schema = 'titan_bbs'
ORDER BY object_schema, object_name;
"
```

## 监控与运维

### 查看 MySQL 状态

```bash
# 查看 MySQL 版本
docker exec -it titan-bbs-mysql mysql -V

# 查看系统变量
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "SHOW VARIABLES;"

# 查看特定变量
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "SHOW VARIABLES LIKE 'max_connections';"

# 查看状态变量
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "SHOW STATUS;"

# 查看字符集设置
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "SHOW VARIABLES LIKE 'character%';"
```

### 查看二进制日志

```bash
# 查看二进制日志列表
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "SHOW BINARY LOGS;"

# 查看当前使用的二进制日志
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "SHOW MASTER STATUS;"

# 查看二进制日志内容（需要指定日志文件名）
docker exec -it titan-bbs-mysql mysqlbinlog /var/lib/mysql/mysql-bin.000001

# 清理旧的二进制日志（保留最近 3 天）
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "PURGE BINARY LOGS BEFORE DATE_SUB(NOW(), INTERVAL 3 DAY);"
```

### 查看错误日志

```bash
# 查看错误日志
docker logs titan-bbs-mysql

# 查看错误日志文件
docker exec -it titan-bbs-mysql cat /var/log/mysql/error.log

# 实时监控错误日志
docker exec -it titan-bbs-mysql tail -f /var/log/mysql/error.log
```

## 故障排查

### 连接问题

```bash
# 测试连接
docker exec -it titan-bbs-mysql mysqladmin -utitan_bbs -ptitan_bbs_2025 ping

# 查看连接错误
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "SHOW STATUS LIKE 'Aborted_connects';"

# 检查端口
docker port titan-bbs-mysql
```

### 性能问题

```bash
# 查看锁等待
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "
SELECT * FROM information_schema.innodb_locks;
SELECT * FROM information_schema.innodb_lock_waits;
"

# 查看长时间运行的查询
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "
SELECT * FROM information_schema.processlist
WHERE command != 'Sleep'
  AND time > 10
ORDER BY time DESC;
"
```

### 磁盘空间问题

```bash
# 查看数据目录大小
docker exec -it titan-bbs-mysql du -sh /var/lib/mysql

# 查看表空间使用情况
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "
SELECT
  table_schema,
  SUM(data_length + index_length) / 1024 / 1024 AS 'Size (MB)'
FROM information_schema.tables
GROUP BY table_schema;
"
```

## 安全建议

### 生产环境

1. **修改默认密码** - 使用强密码（16 位以上）
2. **限制 Root 访问** - 禁止 root 用户远程登录
3. **启用 SSL/TLS** - 加密数据传输
4. **最小权限原则** - 应用只授予必要的权限
5. **定期备份** - 自动化备份并异地存储
6. **监控告警** - 监控慢查询、连接数、磁盘空间等
7. **防火墙规则** - 限制 3306 端口访问来源

### 开发环境

当前配置已满足开发需求，密码保持默认即可。

## 常见问题

### Q1: 如何修改 MySQL 密码？

```bash
# 修改应用用户密码
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "
ALTER USER 'titan_bbs'@'%' IDENTIFIED BY 'new_password';
FLUSH PRIVILEGES;
"

# 同时需要修改 docker-compose-middleware.yml 和 application.yaml
```

### Q2: 如何重置 Root 密码？

```bash
# 停止容器
docker-compose -f docker-compose-middleware.yml stop mysql

# 修改 docker-compose-middleware.yml 中的 MYSQL_ROOT_PASSWORD
# 然后删除数据卷重新启动（会丢失数据）
docker volume rm mysqldata
docker-compose -f docker-compose-middleware.yml up -d mysql
```

### Q3: 数据存储在哪里？

数据存储在 Docker 命名卷 `mysqldata` 中：
```bash
docker volume inspect mysqldata
```

### Q4: 如何清空数据库？

```bash
# 方式一：删除所有表
docker exec -it titan-bbs-mysql mysql -utitan_bbs -ptitan_bbs_2025 titan_bbs -e "
SET FOREIGN_KEY_CHECKS = 0;
DROP DATABASE IF EXISTS titan_bbs;
CREATE DATABASE titan_bbs DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
SET FOREIGN_KEY_CHECKS = 1;
"

# 方式二：删除数据卷（完全清空）
docker-compose -f docker-compose-middleware.yml down
docker volume rm mysqldata
docker-compose -f docker-compose-middleware.yml up -d mysql
```

### Q5: 如何查看 MySQL 配置？

```bash
# 查看配置文件
docker exec -it titan-bbs-mysql cat /etc/mysql/conf.d/my.cnf

# 查看运行时配置
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "SHOW VARIABLES;"
```

### Q6: 导入 SQL 文件报错？

常见原因：
- 字符集不匹配：确保 SQL 文件是 UTF-8 编码
- 外键约束：导入前禁用外键检查 `SET FOREIGN_KEY_CHECKS = 0;`
- 权限不足：使用 root 用户导入或授予足够权限

### Q7: 时区不正确？

验证时区设置：
```bash
docker exec -it titan-bbs-mysql mysql -uroot -ptitan_bbs_root_2025 -e "SELECT NOW();"
```

应显示东八区时间（UTC+8）。

## 相关链接

- [MySQL 8.0 官方文档](https://dev.mysql.com/doc/refman/8.0/en/)
- [MyBatis-Plus 文档](https://baomidou.com/)
- [Druid 连接池文档](https://github.com/alibaba/druid/wiki)
