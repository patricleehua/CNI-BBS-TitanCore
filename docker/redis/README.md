# Redis 7.x 配置说明

## 配置概览

- **版本**: Redis 7.x (Alpine Linux)
- **默认密码**: `titan_bbs_2025`
- **默认端口**: 6379
- **持久化**: RDB + AOF 双重持久化
- **内存限制**: 512MB（可调整）
- **数据库数量**: 16 个

## 快速启动

### 启动 Redis

```bash
cd docker
docker-compose -f docker-compose-middleware.yml up -d redis
```

### 验证服务状态

```bash
# 查看容器状态
docker ps | grep titan-bbs-redis

# 查看健康状态
docker inspect --format='{{.State.Health.Status}}' titan-bbs-redis

# 查看日志
docker logs titan-bbs-redis
```

### 连接测试

```bash
# 方式一：使用 redis-cli（容器内）
docker exec -it titan-bbs-redis redis-cli -a titan_bbs_2025 ping
# 输出：PONG

# 方式二：测试基本操作
docker exec -it titan-bbs-redis redis-cli -a titan_bbs_2025
> SET test "Hello CNI-BBS"
> GET test
> DEL test
> EXIT
```

## 配置文件详解

### 安全配置

```conf
# 密码认证
requirepass titan_bbs_2025

# 禁用危险命令（生产环境推荐）
rename-command FLUSHDB ""    # 禁止清空单个数据库
rename-command FLUSHALL ""   # 禁止清空所有数据库
rename-command CONFIG ""     # 禁止运行时修改配置
```

**生产环境建议**：
- 修改 `requirepass` 为强密码（16 位以上，包含大小写字母、数字、特殊字符）
- 保持危险命令禁用

### 持久化配置

#### RDB 快照（默认启用）

```conf
save 900 1      # 15 分钟内至少 1 个 key 变化
save 300 10     # 5 分钟内至少 10 个 key 变化
save 60 10000   # 1 分钟内至少 10000 个 key 变化
```

**特点**：
- ✅ 文件小，恢复快
- ✅ 性能影响小
- ❌ 可能丢失最后几分钟数据

#### AOF 日志（默认启用）

```conf
appendonly yes
appendfsync everysec  # 每秒同步一次
```

**特点**：
- ✅ 数据更安全，最多丢失 1 秒数据
- ✅ 可读性强，便于修复
- ❌ 文件较大

**推荐策略**：RDB + AOF 双开（当前配置）

### 内存管理

```conf
maxmemory 512mb              # 最大内存限制
maxmemory-policy allkeys-lru # LRU 淘汰策略
```

**淘汰策略说明**：
- `allkeys-lru`: 从所有 key 中删除最近最少使用的（推荐）
- `volatile-lru`: 只从设置了过期时间的 key 中删除
- `allkeys-random`: 随机删除任意 key
- `volatile-ttl`: 删除即将过期的 key
- `noeviction`: 不删除，写满后返回错误

### 性能优化

```conf
# 慢查询阈值：10ms
slowlog-log-slower-than 10000

# TCP 优化
tcp-backlog 511
tcp-keepalive 300

# 最大客户端连接数
maxclients 10000
```

## 在 Java 项目中使用

### 配置文件设置

在 `application-docker.yaml` 中配置（Docker 环境）：

```yaml
titan:
  redis:
    host: titan-bbs-redis    # 容器名称（Docker 网络内部）
    port: 6379
    database: 0
    password: titan_bbs_2025
```

在 `application-dev.yaml` 中配置（本地开发）：

```yaml
titan:
  redis:
    host: localhost          # 或 127.0.0.1
    port: 6379
    database: 0
    password: titan_bbs_2025
```

### 代码示例

项目已集成 Spring Data Redis，直接使用即可：

```java
@Autowired
private RedisTemplate<String, Object> redisTemplate;

// 基本操作
redisTemplate.opsForValue().set("key", "value");
String value = (String) redisTemplate.opsForValue().get("key");

// 设置过期时间
redisTemplate.opsForValue().set("key", "value", 30, TimeUnit.MINUTES);

// Hash 操作
redisTemplate.opsForHash().put("hash", "field", "value");

// List 操作
redisTemplate.opsForList().rightPush("list", "item");
```

## 数据管理

### 备份数据

```bash
# 手动触发 RDB 快照
docker exec titan-bbs-redis redis-cli -a titan_bbs_2025 BGSAVE

# 复制备份文件到宿主机
docker cp titan-bbs-redis:/data/dump.rdb ./backup/
docker cp titan-bbs-redis:/data/appendonly.aof ./backup/
```

### 恢复数据

```bash
# 停止容器
docker-compose -f docker-compose-middleware.yml stop redis

# 复制备份文件到数据卷
docker run --rm -v redisdata:/data -v $(pwd)/backup:/backup \
  alpine sh -c "cp /backup/dump.rdb /data/ && cp /backup/appendonly.aof /data/"

# 启动容器
docker-compose -f docker-compose-middleware.yml start redis
```

### 清空数据（谨慎操作）

```bash
# 方式一：删除数据卷（完全清空）
docker-compose -f docker-compose-middleware.yml down -v
docker volume rm redisdata

# 方式二：清空所有数据库（需先解除命令禁用）
# 注意：当前配置已禁用 FLUSHALL 命令
```

## 监控与运维

### 查看 Redis 信息

```bash
# 服务器信息
docker exec titan-bbs-redis redis-cli -a titan_bbs_2025 INFO server

# 内存使用情况
docker exec titan-bbs-redis redis-cli -a titan_bbs_2025 INFO memory

# 客户端连接信息
docker exec titan-bbs-redis redis-cli -a titan_bbs_2025 INFO clients

# 持久化状态
docker exec titan-bbs-redis redis-cli -a titan_bbs_2025 INFO persistence

# 统计信息
docker exec titan-bbs-redis redis-cli -a titan_bbs_2025 INFO stats
```

### 慢查询日志

```bash
# 查看慢查询日志
docker exec titan-bbs-redis redis-cli -a titan_bbs_2025 SLOWLOG GET 10

# 查看慢查询总数
docker exec titan-bbs-redis redis-cli -a titan_bbs_2025 SLOWLOG LEN

# 清空慢查询日志
docker exec titan-bbs-redis redis-cli -a titan_bbs_2025 SLOWLOG RESET
```

### 数据库统计

```bash
# 查看所有数据库的 key 数量
docker exec titan-bbs-redis redis-cli -a titan_bbs_2025 INFO keyspace

# 查看当前数据库 key 数量
docker exec titan-bbs-redis redis-cli -a titan_bbs_2025 DBSIZE

# 查看所有 key
docker exec titan-bbs-redis redis-cli -a titan_bbs_2025 --scan

# 按模式查找 key
docker exec titan-bbs-redis redis-cli -a titan_bbs_2025 --scan --pattern "user:*"
```

## 环境变量配置

支持通过环境变量自定义配置（在 `.env` 文件中设置）：

```env
# Redis 端口映射
REDIS_PORT=6379

# 如需修改密码，需同时修改：
# 1. docker/redis/redis.conf 中的 requirepass
# 2. docker-compose-middleware.yml 中的 healthcheck 命令
# 3. application-*.yaml 中的 titan.redis.password
```

## 安全建议

### 生产环境

1. **修改默认密码**：将 `titan_bbs_2025` 改为强密码
2. **限制网络访问**：不要暴露 6379 端口到公网
3. **启用 TLS**：生产环境建议启用 SSL/TLS
4. **定期备份**：建议每天备份 RDB 和 AOF 文件
5. **监控告警**：监控内存使用、慢查询、连接数等指标

### 开发环境

当前配置已满足开发需求，密码保持默认即可。

## 常见问题

### Q1: 如何修改 Redis 密码？

需要修改三处：

1. `docker/redis/redis.conf`: 修改 `requirepass` 值
2. `docker-compose-middleware.yml`: 修改 healthcheck 中的密码
3. `application-*.yaml`: 修改 `titan.redis.password`

然后重启容器：
```bash
docker-compose -f docker-compose-middleware.yml restart redis
```

### Q2: 如何调整内存限制？

修改 `docker/redis/redis.conf` 中的 `maxmemory` 值，然后重启容器。

### Q3: 数据会丢失吗？

不会。使用了 Docker 命名卷 `redisdata`，数据持久化存储。即使删除容器，数据卷也会保留。

### Q4: 如何完全清空 Redis 数据？

```bash
docker-compose -f docker-compose-middleware.yml down
docker volume rm redisdata
docker-compose -f docker-compose-middleware.yml up -d redis
```

### Q5: 如何查看 Redis 版本？

```bash
docker exec titan-bbs-redis redis-cli -a titan_bbs_2025 INFO server | grep redis_version
```

## 性能基准测试

使用 redis-benchmark 测试性能：

```bash
# 基础测试（10 万次请求）
docker exec titan-bbs-redis redis-benchmark -a titan_bbs_2025 -n 100000 -q

# 测试特定命令
docker exec titan-bbs-redis redis-benchmark -a titan_bbs_2025 -t set,get -n 100000 -q

# 使用 pipeline 测试
docker exec titan-bbs-redis redis-benchmark -a titan_bbs_2025 -n 100000 -q -P 16
```

## 相关链接

- [Redis 官方文档](https://redis.io/documentation)
- [Redis 命令参考](https://redis.io/commands)
- [Spring Data Redis 文档](https://spring.io/projects/spring-data-redis)
