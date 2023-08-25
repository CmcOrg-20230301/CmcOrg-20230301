package com.cmcorg20230301.be.engine.file.minio.configuration;

import com.cmcorg20230301.be.engine.file.minio.properties.FileMinioProperties;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MinIoClientConfiguration {

    @Bean
    public MinioClient minioClient(FileMinioProperties fileMinioProperties) {

        log.info("minio 启动：{}", fileMinioProperties.getUploadEndpoint());

        return MinioClient.builder().endpoint(fileMinioProperties.getUploadEndpoint())
            .credentials(fileMinioProperties.getAccessKey(), fileMinioProperties.getSecretKey()).build();

    }

}
