package com.cmcorg20230301.engine.be.generate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class BeGenerateApplication {

    public static void main(String[] args) {
        SpringApplication.run(BeGenerateApplication.class, args);
    }

}
