package com.titancore.config.rabbitmq;

public class RabbitMqConstant {
    public static final String DEFAULT_PREFIX = "cni.core.";

    /**
     * 主题交换机
     */
    public static final String EXCHANGE_TOPIC = DEFAULT_PREFIX + "exchange-topic";
    public static final String QUEUE_TOPIC_GROUP_CHAT = DEFAULT_PREFIX + "queue.topic.group_chat";
    public static final String QUEUE_TOPIC_PRIVATE_CHAT = DEFAULT_PREFIX + "queue.topic.private_chat";
    public static final String QUEUE_TOPIC_BROAD_CAST = DEFAULT_PREFIX + "queue.topic.broad_cast";
    public static final String ROUTING_TOPIC_GROUP_CHAT = DEFAULT_PREFIX + "routing.topic.group_chat";
    public static final String ROUTING_TOPIC_PRIVATE_CHAT = DEFAULT_PREFIX + "routing.topic.private_chat";
    public static final String ROUTING_TOPIC_BROAD_CAST = DEFAULT_PREFIX + "routing.topic.broad_cast";

    /**
     * 死信交换机
     */
    public static final String EXCHANGE_DEAD = DEFAULT_PREFIX + "exchange-dlx";
    public static final String QUEUE_DEAD_GROUP_CHAT = DEFAULT_PREFIX + "queue.group_chat_dlq";
    public static final String QUEUE_DEAD_PRIVATE_CHAT = DEFAULT_PREFIX + "queue.private_chat_dlq";
    public static final String QUEUE_DEAD_BROADCAST = DEFAULT_PREFIX + "queue.broadcast_dlq";

    public static final String ROUTING_DEAD_GROUP_CHAT = DEFAULT_PREFIX + "routing.group_chat_dlq";
    public static final String ROUTING_DEAD_PRIVATE_CHAT = DEFAULT_PREFIX + "routing.private_chat_dlq";
    public static final String ROUTING_DEAD_BROADCAST = DEFAULT_PREFIX + "routing.broadcast_dlq";

    /**
     * 延迟交换机
     */
    public static final String EXCHANGE_DELAY = DEFAULT_PREFIX + "exchange-delay";
    public static final String QUEUE_DELAY_1 = DEFAULT_PREFIX + "queue.delay_1";
    public static final String ROUTING_DELAY = DEFAULT_PREFIX + "routing.delay";
}
