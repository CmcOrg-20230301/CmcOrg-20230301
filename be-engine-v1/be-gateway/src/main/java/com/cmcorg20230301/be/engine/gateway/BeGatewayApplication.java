package com.cmcorg20230301.be.engine.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BeGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeGatewayApplication.class, args);
    }

}
