package com.titancore.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TopicExchangeConfig {

    /**
     * 定义一个主题类型的交换机
     * @return
     */
    @Bean(RabbitMqConstant.EXCHANGE_TOPIC)
    public Exchange topicExchange() {
        return ExchangeBuilder
                .topicExchange(RabbitMqConstant.EXCHANGE_TOPIC)
                .durable(true)
                .build();
    }

    /**
     * 群聊消息队列
     * @return
     */
    @Bean(RabbitMqConstant.QUEUE_TOPIC_GROUP_CHAT)
    public Queue groupChatQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", RabbitMqConstant.EXCHANGE_DEAD);
        args.put("x-dead-letter-routing-key",RabbitMqConstant.ROUTING_DEAD_GROUP_CHAT);
        return QueueBuilder.durable(RabbitMqConstant.QUEUE_TOPIC_GROUP_CHAT).withArguments(args).build();
    }

    /**
     * 私聊消息队列
     * @return
     */
    @Bean(RabbitMqConstant.QUEUE_TOPIC_PRIVATE_CHAT)
    public Queue privateChatQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", RabbitMqConstant.EXCHANGE_DEAD);
        args.put("x-dead-letter-routing-key", RabbitMqConstant.ROUTING_DEAD_PRIVATE_CHAT);
        return QueueBuilder.durable(RabbitMqConstant.QUEUE_TOPIC_PRIVATE_CHAT).withArguments(args).build();
    }

    /**
     * 广播消息队列
     * @return
     */
    @Bean(RabbitMqConstant.QUEUE_TOPIC_BROAD_CAST)
    public Queue broadcastQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", RabbitMqConstant.EXCHANGE_DEAD);
        args.put("x-dead-letter-routing-key", RabbitMqConstant.ROUTING_DEAD_BROADCAST);
        return QueueBuilder.durable(RabbitMqConstant.QUEUE_TOPIC_BROAD_CAST).withArguments(args).build();
    }

    /**
     * 绑定群聊队列到交换机
     *
     * @param exchange //交换机
     * @param queue    //队列
     * @return
     */
    @Bean
    public Binding bindGroupChatQueue(@Qualifier(RabbitMqConstant.EXCHANGE_TOPIC) Exchange exchange,
                                      @Qualifier(RabbitMqConstant.QUEUE_TOPIC_GROUP_CHAT) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(RabbitMqConstant.ROUTING_TOPIC_GROUP_CHAT)
                .noargs();
    }

    /**
     * 绑定私聊队列到交换机
     *
     * @param exchange //交换机
     * @param queue    //队列
     * @return
     */
    @Bean
    public Binding bindPrivateChatQueue(@Qualifier(RabbitMqConstant.EXCHANGE_TOPIC) Exchange exchange,
                                        @Qualifier(RabbitMqConstant.QUEUE_TOPIC_PRIVATE_CHAT) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(RabbitMqConstant.ROUTING_TOPIC_PRIVATE_CHAT)
                .noargs();
    }

    /**
     * 绑定广播队列到交换机
     *
     * @param exchange //交换机
     * @param queue    //队列
     * @return
     */
    @Bean
    public Binding bindBroadcastQueue(@Qualifier(RabbitMqConstant.EXCHANGE_TOPIC) Exchange exchange,
                                      @Qualifier(RabbitMqConstant.QUEUE_TOPIC_BROAD_CAST) Queue queue) {
        return BindingBuilder
                .bind(queue)
                .to(exchange)
                .with(RabbitMqConstant.ROUTING_TOPIC_BROAD_CAST)
                .noargs();
    }
}
