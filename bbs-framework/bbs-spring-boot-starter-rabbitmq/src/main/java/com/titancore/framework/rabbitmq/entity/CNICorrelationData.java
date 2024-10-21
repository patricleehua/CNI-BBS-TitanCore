package com.titancore.framework.rabbitmq.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.amqp.rabbit.connection.CorrelationData;

@EqualsAndHashCode(callSuper = true)
@Data
public class CNICorrelationData extends CorrelationData {

    private Object message;
    private String exchange;
    private String routingKey;

    private int retryCount=0;
    private boolean isDelay = false;
    private int delayTime = 10;

}
