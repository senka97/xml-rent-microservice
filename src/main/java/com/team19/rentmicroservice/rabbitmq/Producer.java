package com.team19.rentmicroservice.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Producer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void addToMessageQueue(String routingkey, MessageMQ message) {
        ObjectMapper objectMapper = new ObjectMapper();
        String messageJson = null;
        try {
            messageJson = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        this.rabbitTemplate.convertAndSend(routingkey, messageJson);
    }
}
