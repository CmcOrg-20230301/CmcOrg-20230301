package com.cmcorg20230301.engine.be.file.minio.configuration;

import com.cmcorg20230301.engine.be.file.minio.properties.FileMinioProperties;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MinIoClientConfiguration {

    @Bean
    public MinioClient minioClient(FileMinioProperties fileMinioProperties) {

        log.info("minio 启动：{}", fileMinioProperties.getEndpoint());

        return MinioClient.builder().endpoint(fileMinioProperties.getEndpoint())
            .credentials(fileMinioProperties.getAccessKey(), fileMinioProperties.getBucketName()).build();

    }

}
