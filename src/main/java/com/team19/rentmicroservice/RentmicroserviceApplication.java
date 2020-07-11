package com.team19.rentmicroservice;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class RentmicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentmicroserviceApplication.class, args);
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();

        connectionFactory.setHost("orangutan.rmq.cloudamqp.com ");
        connectionFactory.setPassword("WDdTEeUjMPkUrV8ipbpbuSliBMJYVMi-");
        connectionFactory.setUri("amqp://nzlqzoub:WDdTEeUjMPkUrV8ipbpbuSliBMJYVMi-@orangutan.rmq.cloudamqp.com/nzlqzoub");

        return connectionFactory;
    }

}
