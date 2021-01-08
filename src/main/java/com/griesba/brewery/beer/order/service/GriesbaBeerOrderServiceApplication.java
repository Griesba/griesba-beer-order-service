package com.griesba.brewery.beer.order.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisAutoConfiguration;

//@SpringBootApplication(exclude = ArtemisAutoConfiguration.class) when using embedded Spring JMS server
@SpringBootApplication
public class GriesbaBeerOrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GriesbaBeerOrderServiceApplication.class, args);
    }

}
