package com.titancore.config.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DelayExchangeConfig {

    /**
     * 延迟交换机
     * @return
     */
    @Bean(RabbitMqConstant.EXCHANGE_DELAY)
    public DirectExchange delayExchange() {
        return ExchangeBuilder
                .directExchange(RabbitMqConstant.EXCHANGE_DELAY) // 指定交换机类型和名称
                .delayed() // 设置delay的属性为true
                .durable(true) // 持久化
                .build();
    }

    @Bean(RabbitMqConstant.QUEUE_DELAY_1)
    public Queue delayedQueue() {
        return new Queue(RabbitMqConstant.QUEUE_DELAY_1);
    }

    @Bean
    public Binding delayQueueBinding(@Qualifier(RabbitMqConstant.EXCHANGE_DELAY) Exchange exchange,
                                     @Qualifier(RabbitMqConstant.QUEUE_DELAY_1) Queue queue) {
        return BindingBuilder.bind(queue).to(exchange).with(RabbitMqConstant.ROUTING_DELAY).noargs();
    }
}
