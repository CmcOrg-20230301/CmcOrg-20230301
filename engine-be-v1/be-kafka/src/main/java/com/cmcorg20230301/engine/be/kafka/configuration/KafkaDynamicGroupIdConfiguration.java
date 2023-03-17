package com.cmcorg20230301.engine.be.kafka.configuration;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class KafkaDynamicGroupIdConfiguration {

    @Value("${server.port:8080}")
    private int port;

    public String getGroupId() {

        String groupId; // 设置：groupId 为 本机 MAC地址 + port，或者 uuid

        try {
            groupId = NetUtil.getLocalMacAddress() + ":" + port;
        } catch (Exception e) {
            groupId = IdUtil.simpleUUID();
        }

        log.info("kafka 动态 groupId：{}", groupId);

        return groupId;

    }

}
