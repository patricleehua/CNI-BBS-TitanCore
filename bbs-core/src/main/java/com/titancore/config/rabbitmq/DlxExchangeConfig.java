package com.titancore.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DlxExchangeConfig {

    /**
     * 死信交换机
     * @return
     */
    @Bean(RabbitMqConstant.EXCHANGE_DEAD)
    public Exchange dlxExchange() {
        return ExchangeBuilder.directExchange(RabbitMqConstant.EXCHANGE_DEAD).durable(true).build();
    }

    /**
     * 死信队列
     */
    @Bean(RabbitMqConstant.QUEUE_DEAD_GROUP_CHAT)
    public Queue groupChatDlxQueue() {
        return QueueBuilder.durable(RabbitMqConstant.QUEUE_DEAD_GROUP_CHAT).build();
    }

    @Bean(RabbitMqConstant.QUEUE_DEAD_PRIVATE_CHAT)
    public Queue privateChatDlxQueue() {
        return QueueBuilder.durable(RabbitMqConstant.QUEUE_DEAD_PRIVATE_CHAT).build();
    }

    @Bean(RabbitMqConstant.QUEUE_DEAD_BROADCAST)
    public Queue broadcastDlxQueue() {
        return QueueBuilder.durable(RabbitMqConstant.QUEUE_DEAD_BROADCAST).build();
    }

    /**
     * 绑定群聊死信队列到死信交换机
     */
    @Bean
    public Binding bindGroupChatDlx(@Qualifier(RabbitMqConstant.EXCHANGE_DEAD) Exchange dlxExchange,
                                    @Qualifier(RabbitMqConstant.QUEUE_DEAD_GROUP_CHAT) Queue dlxQueue) {
        return BindingBuilder.bind(dlxQueue).to(dlxExchange).with(RabbitMqConstant.ROUTING_DEAD_GROUP_CHAT).noargs();
    }

    @Bean
    public Binding bindPrivateChatDlx(@Qualifier(RabbitMqConstant.EXCHANGE_DEAD) Exchange dlxExchange,
                                      @Qualifier(RabbitMqConstant.QUEUE_DEAD_PRIVATE_CHAT) Queue dlxQueue) {
        return BindingBuilder.bind(dlxQueue).to(dlxExchange).with(RabbitMqConstant.ROUTING_DEAD_PRIVATE_CHAT).noargs();
    }

    @Bean
    public Binding bindBroadcastDlx(@Qualifier(RabbitMqConstant.EXCHANGE_DEAD) Exchange dlxExchange,
                                    @Qualifier(RabbitMqConstant.QUEUE_DEAD_BROADCAST) Queue dlxQueue) {
        return BindingBuilder.bind(dlxQueue).to(dlxExchange).with(RabbitMqConstant.ROUTING_DEAD_BROADCAST).noargs();
    }
}
