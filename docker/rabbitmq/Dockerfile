# 下载说明：
# 请先下载插件文件到此目录（与 Dockerfile 同级）：
# https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/v3.12.0/rabbitmq_delayed_message_exchange-3.12.0.ez
#
# 如果下载速度慢，可以使用镜像加速：
# https://mirror.ghproxy.com/https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/v3.12.0/rabbitmq_delayed_message_exchange-3.12.0.ez

FROM docker.io/library/rabbitmq:3.12-management

# 复制延迟消息队列插件到 plugins 目录
# 注意：官方镜像默认使用 rabbitmq 用户（UID 999），plugins 目录对该用户可写
COPY --chown=rabbitmq:rabbitmq rabbitmq_delayed_message_exchange-3.12.0.ez /plugins/

# 复制插件配置文件（容器启动时会自动读取此文件并启用插件）
COPY --chown=rabbitmq:rabbitmq enabled_plugins /etc/rabbitmq/enabled_plugins

# 复制 RabbitMQ 配置文件
COPY --chown=rabbitmq:rabbitmq rabbitmq.conf /etc/rabbitmq/rabbitmq.conf

# 不需要显式切换用户，官方镜像默认就是 rabbitmq 用户
# 插件会在容器启动时通过 enabled_plugins 文件自动启用，无需在构建时执行
