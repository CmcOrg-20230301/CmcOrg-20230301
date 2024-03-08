package com.cmcorg20230301.be.engine.milvus.configuration;

import javax.annotation.Resource;

import org.jetbrains.annotations.Nullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cmcorg20230301.be.engine.milvus.properties.MilvusProperties;

import cn.hutool.core.util.StrUtil;
import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class MilvusConfiguration {

    @Resource
    MilvusProperties milvusProperties;

    @Nullable
    @Bean
    public MilvusServiceClient milvusServiceClient() {

        if (StrUtil.isBlank(milvusProperties.getHost()) || milvusProperties.getPort() == null) {
            return null;
        }

        ConnectParam.Builder builder =
            ConnectParam.newBuilder().withHost(milvusProperties.getHost()).withPort(milvusProperties.getPort());

        if (StrUtil.isNotBlank(milvusProperties.getUsername())) {

            builder.withAuthorization(milvusProperties.getUsername(), milvusProperties.getPassword());

        }

        log.info("milvus 启动：{}:{}", milvusProperties.getHost(), milvusProperties.getPort());

        return new MilvusServiceClient(builder.build());

    }

}
