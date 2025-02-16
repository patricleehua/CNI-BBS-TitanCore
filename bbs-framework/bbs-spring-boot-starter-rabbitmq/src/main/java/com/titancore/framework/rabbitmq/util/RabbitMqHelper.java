package com.titancore.framework.rabbitmq.util;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.titancore.framework.rabbitmq.entity.CNICorrelationData;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class RabbitMqHelper {

    public static final String TEMPORARY_MESSAGE_PRIX = "rabbitmq:temporary:message:";
    public static final Long TEMPORARY_MESSAGE_TTL = 10L;
    public static final String DEAL_MESSAGE_PRIX = "rabbitmq:deal:message:";
    public static final Long DEAL_MESSAGE_TTL = 10L;


    private final RabbitTemplate rabbitTemplate;

    private final StringRedisTemplate stringRedisTemplate;
    @PostConstruct
    private void setupConfirmAndReturnCallbacks() {
        /**
         * 只确认消息是否正确到达 Exchange 中,成功与否都会回调
         *
         * @param correlation 相关数据  非消息本身业务数据
         * @param ack             应答结果
         * @param cause           如果发送消息到交换器失败，错误原因
         */
        rabbitTemplate.setConfirmCallback((correlation, ack, cause) -> {
            if (ack) {
                log.debug("消息发送到Exchange成功：{}", correlation);
            } else {
                log.error("消息发送到Exchange失败：{}", cause);
                //执行消息重发
                this.retrySendMsg(correlation);
            }
        });

        /**
         * 消息没有正确到达队列时触发回调，如果正确到达队列不执行
         */
        rabbitTemplate.setReturnsCallback(returned -> {
            //当路由队列失败 也需要重发
            //1.构建相关数据对象
            String redisKey = returned.getMessage().getMessageProperties().getHeader("spring_returned_message_correlation");
            //为普通消息
            if(redisKey == null){
                return;
            }
            String correlationDataStr = stringRedisTemplate.opsForValue().get(redisKey);
            CNICorrelationData cniCorrelationData = JSON.parseObject(correlationDataStr, CNICorrelationData.class);
            //延迟消息重试：如果不考虑延迟消息重发 直接返回
            if (cniCorrelationData != null && cniCorrelationData.isDelay()) {
                return;
            }
            //2.调用消息重发方法
            log.error("消息没有正确到达队列\n触发return callback: {}\nreplyCode: {}\nreplyText: {}\nexchange/rk: {}/{}", returned.getMessage(), returned.getReplyCode(), returned.getReplyText(), returned.getExchange(), returned.getRoutingKey());
            this.retrySendMsg(cniCorrelationData);
        });
    }
    /**
     * 发送普通消息
     * @param exchange
     * @param routingKey
     * @param msg
     */
    public boolean sendMessage(String exchange, String routingKey, Object msg){
        log.debug("准备发送消息，exchange:{}, routingKey:{}, msg:{}", exchange, routingKey, msg);
        rabbitTemplate.convertAndSend(exchange, routingKey, msg);
        return true;
    }

    /**
     * 发送可重试消息
     * @param exchange
     * @param routingKey
     * @param msg
     * @return
     */
    public boolean sendMessageRetry(String exchange, String routingKey, Object msg){
        log.info("发送可重试消息，exchange:{}, routingKey:{}, msg:{}", exchange, routingKey, msg);
        //1.创建自定义相关消息对象-包含业务数据本身，交换器名称，路由键，队列类型，延迟时间,重试次数
        CNICorrelationData correlationData = new CNICorrelationData();
        String redisKey = TEMPORARY_MESSAGE_PRIX + UUID.randomUUID().toString().replace("-", "");
        correlationData.setId(redisKey);
        correlationData.setMessage(msg);
        correlationData.setExchange(exchange);
        correlationData.setRoutingKey(routingKey);

        //2.将相关消息封装到发送消息方法中
        rabbitTemplate.convertAndSend(exchange, routingKey, msg,correlationData);

        //3.将相关消息存入Redis  Key：UUID  相关消息对象  10 分钟
        stringRedisTemplate.opsForValue().set(redisKey, JSON.toJSONString(correlationData),TEMPORARY_MESSAGE_TTL, TimeUnit.MINUTES);
        return true;
    }
    /**
     * 发送延迟消息 (插件实现，记得在rabbitmq安装延迟插件）
     * @param exchange
     * @param routingKey
     * @param msg
     * @return
     */
    public boolean sendDelayMessage(String exchange, String routingKey, Object msg, int delayTime){
        //1.创建自定义相关消息对象-包含业务数据本身，交换器名称，路由键，队列类型，延迟时间,重试次数
        CNICorrelationData correlationData = new CNICorrelationData();
        String uuid = TEMPORARY_MESSAGE_PRIX + UUID.randomUUID().toString().replaceAll("-", "");
        correlationData.setId(uuid);
        correlationData.setMessage(msg);
        correlationData.setExchange(exchange);
        correlationData.setRoutingKey(routingKey);
        correlationData.setDelay(true);
        correlationData.setDelayTime(delayTime);

        //2.将相关消息存入Redis  Key：UUID  相关消息对象  10 分钟
        stringRedisTemplate.opsForValue().set(uuid, JSON.toJSONString(correlationData), 10, TimeUnit.MINUTES);
        //3.将相关消息封装到发送消息方法中
        rabbitTemplate.convertAndSend(exchange, routingKey, msg, message -> {
            // 使用消息头设置延迟时间
            message.getMessageProperties().setHeader("x-delay", delayTime * 1000);
            return message;
        },correlationData);
        return true;
    }


    /**
     * 消息重新发送方法
     *
     * @param correlationData
     */
    private void retrySendMsg(CorrelationData correlationData) {
        //获取相关数据
        CNICorrelationData cniCorrelationData = (CNICorrelationData) correlationData;

        //获取redis中存放重试次数
        //先重发，在写会到redis中次数
        int retryCount = cniCorrelationData.getRetryCount();
        if (retryCount >= 3) {
            //超过最大重试次数
            log.error("生产者超过最大重试次数，记录失败消息，等待定时任务处理中...");
            String redisKey = DEAL_MESSAGE_PRIX + cniCorrelationData.getId().substring(TEMPORARY_MESSAGE_PRIX.length());
            stringRedisTemplate.opsForValue().set(redisKey,JSON.toJSONString(cniCorrelationData));
            return;
        }
        //重发次数+1
        retryCount += 1;
        cniCorrelationData.setRetryCount(retryCount);
        stringRedisTemplate.opsForValue().set(cniCorrelationData.getId(), JSON.toJSONString(cniCorrelationData), TEMPORARY_MESSAGE_TTL, TimeUnit.MINUTES);
        log.info("进行消息重发！");
        //重发消息
        //如果是延迟消息，依然需要设置消息延迟时间
        if (cniCorrelationData.isDelay()) {
            //延迟消息
            rabbitTemplate.convertAndSend(cniCorrelationData.getExchange(), cniCorrelationData.getRoutingKey(), cniCorrelationData.getMessage(), message -> {
                message.getMessageProperties().setHeader("x-delay", cniCorrelationData.getDelayTime() * 1000);
                return message;
            }, cniCorrelationData);
        } else {
            //可重发消息
            rabbitTemplate.convertAndSend(cniCorrelationData.getExchange(), cniCorrelationData.getRoutingKey(), cniCorrelationData.getMessage(), cniCorrelationData);
        }
    }
}