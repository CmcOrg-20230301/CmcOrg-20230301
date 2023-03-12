package com.cmcorg20230301.engine.be.redisson.configuration;

import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.ReadMode;
import org.redisson.spring.starter.RedissonAutoConfigurationCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRedissonConfigurationCustomizer implements RedissonAutoConfigurationCustomizer {

    @Override
    public void customize(Config config) {
        config.setCodec(new JsonJacksonCodec()); // 设置为：json序列化，目的：方便看
        if (config.isClusterConfig()) {
            config.useClusterServers().setReadMode(ReadMode.MASTER); // 默认为 SLAVE，会出现延迟的情况
        }
    }

}
