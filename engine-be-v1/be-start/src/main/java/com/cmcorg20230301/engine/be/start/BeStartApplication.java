package com.cmcorg20230301.engine.be.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BeStartApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeStartApplication.class, args);
    }

}
