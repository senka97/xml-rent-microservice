package com.team19.rentmicroservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RentmicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentmicroserviceApplication.class, args);
    }

}
