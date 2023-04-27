package com.cmcorg20230301.engine.be.file.aliyun.configuration;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.cmcorg20230301.engine.be.file.aliyun.properties.FileAliYunProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class AliYunOSSConfiguration {

    @Bean
    public OSS oss(FileAliYunProperties fileAliYunProperties) {

        log.info("阿里云oss 启动：{}", fileAliYunProperties.getEndpoint());

        return new OSSClientBuilder().build(fileAliYunProperties.getEndpoint(), fileAliYunProperties.getAccessKeyId(),
            fileAliYunProperties.getAccessKeySecret());

    }

}
