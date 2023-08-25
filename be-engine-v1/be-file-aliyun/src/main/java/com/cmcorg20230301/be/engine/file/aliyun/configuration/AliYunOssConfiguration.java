package com.cmcorg20230301.be.engine.file.aliyun.configuration;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.cmcorg20230301.be.engine.file.aliyun.properties.FileAliYunProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AliYunOssConfiguration {

    @Bean
    public OSS oss(FileAliYunProperties fileAliYunProperties) {

        log.info("阿里云oss 启动：{}", fileAliYunProperties.getUploadEndpoint());

        return new OSSClientBuilder()
            .build(fileAliYunProperties.getUploadEndpoint(), fileAliYunProperties.getAccessKey(),
                fileAliYunProperties.getSecretKey());

    }

}
