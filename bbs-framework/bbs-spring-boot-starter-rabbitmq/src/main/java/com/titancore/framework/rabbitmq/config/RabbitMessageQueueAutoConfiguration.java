package com.titancore.framework.rabbitmq.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.titancore.framework.rabbitmq.util.RabbitMqHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@ConditionalOnClass(value = {MessageConverter.class, RabbitTemplate.class})
@Slf4j
public class RabbitMessageQueueAutoConfiguration {

    /**
     * 自定义消息转换器
     * @param mapper
     * @return
     */
    @Bean
    @ConditionalOnBean(ObjectMapper.class)
    public MessageConverter messageConverter(ObjectMapper mapper){
        log.info("========初始化MessageConverter自定义消息转换器========");
        // 1.定义消息转换器
        Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter(mapper);
        // 2.配置自动创建消息id，用于识别不同消息
        jackson2JsonMessageConverter.setCreateMessageIds(true);
        return jackson2JsonMessageConverter;
    }

    /**
     * 自定义工具类
     * @param rabbitTemplate
     * @return
     */
    @Bean
    public RabbitMqHelper rabbitMqHelper(RabbitTemplate rabbitTemplate, StringRedisTemplate stringRedisTemplate){
        log.info("========初始化RabbitMqHelper自定义工具类========");
        return new RabbitMqHelper(rabbitTemplate,stringRedisTemplate);
    }


}
